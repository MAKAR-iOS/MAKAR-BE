package makar.dev.manager;

import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.domain.LineMap;
import makar.dev.domain.LineStation;
import makar.dev.domain.SubRoute;
import makar.dev.domain.data.SubwaySchedule;
import makar.dev.domain.data.TimeInfo;
import makar.dev.repository.LineMapRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class MakarManager {
    private static final int UPTOWN = 1;  //상행
    private static final int DOWNTOWN = 2; //하행

    private final APIManager apiManager;
    private final LineMapRepository lineMapRepository;


    //경로 정보와 지하철 시간표를 활용하여 막차 시간을 계산한다.
    public Calendar computeMakarTime(List<SubRoute> subRouteList, int dayOfWeek) {
        Calendar takingTime = null;

        List<Date> makarTimes = new ArrayList<>();
        // 경로를 거꾸로 순회하면서 막차 시간 구하기
        for (int i = subRouteList.size() - 1; i >= 0; i--) {
            SubRoute lastSubRoute = subRouteList.get(i);

            //startStation의 지하철 시간표 호출하기
            SubwaySchedule subwaySchedule = apiManager.requestSubwaySchedule(lastSubRoute.getFromStationCode(), lastSubRoute.getWayCode());

            //마지막 서브 경로 or 단일 서브 경로인 경우
            if (i == subRouteList.size() - 1) {
                takingTime = computeLastMakarTime(dayOfWeek, subwaySchedule, lastSubRoute.getLineNum(), lastSubRoute.getWayCode(), lastSubRoute.getFromStationCode(), lastSubRoute.getToStationCode());
            } else {
                int sectionTime = lastSubRoute.getSectionTime();
                sectionTime += lastSubRoute.getTransferTime();
                takingTime.add(Calendar.MINUTE, -sectionTime);

                // 환승시간을 포함하여 이후 서브 경로의 막차 시간 구하기
                takingTime = computeTransferMakarTime(takingTime, dayOfWeek, subwaySchedule, lastSubRoute.getLineNum(), lastSubRoute.getWayCode(), lastSubRoute.getFromStationCode(), lastSubRoute.getToStationCode());
            }

            makarTimes.add(0, takingTime.getTime());
        }
        return takingTime;
    }

    public Calendar computeLastMakarTime(int dayOfWeek, SubwaySchedule subwaySchedule, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);

        //매시간마다
        for (int i = time.size() - 1; i >= 0; i--) {
            SubwaySchedule.OrdList.TimeDirection.TimeData timeData = time.get(i);
            List<TimeInfo> timeInfos = TimeInfo.parseTimeString(timeData.getList());

            AtomicBoolean canGoInSubway = new AtomicBoolean(false);
            List<CompletableFuture<Void>> tasks = new ArrayList<>();

            AtomicReference<TimeInfo> result = new AtomicReference<>();
            //분마다
            for (int j = timeInfos.size() - 1; j >= 0; j--) {
                TimeInfo timeInfo = timeInfos.get(j);

                //해당 호선의 지하철 노선도 데이터 가져오기
                CompletableFuture<Void> task = new CompletableFuture<>();
                LineMap lineMap = lineMapRepository.findByLineNum(odsayLaneType)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_LINE_MAP));

                int startIndex = -1;
                int endIndex = -1;
                int terminalIndex = -1;

                List<LineStation> stationList = new ArrayList<>();
                if (wayCode == UPTOWN) {
                    stationList = lineMap.getUpLineList();
                } else if (wayCode == DOWNTOWN) {
                    stationList = lineMap.getDownLineList();
                } else {
                    throw new GeneralException(ErrorStatus.NOT_FOUND_LINE_STATION);
                }

                //순회하면서 출발역, 도착역, 종착역의 index구하기
                for (int k = 0; k < stationList.size(); k++) {
                    LineStation lineStation = stationList.get(k);

                    String stationName = lineStation.getStationName();
                    if (stationName.equals(timeInfo.getTerminalStation())) {
                        terminalIndex = k;
                    }

                    int odsayStationID = lineStation.getOdsayStationId();
                    if (odsayStationID == fromStationID && startIndex == -1) {
                        startIndex = k;
                    }

                    //순환 경로인 경우 도착역은 뒤의 인덱스로 바뀌게
                    if (odsayStationID == toStationID) {
                        endIndex = k;
                    }
                }

                //index가 출발역, 도착역, 종착역순이면 해당 열차를 탈 수 있다.
                if (startIndex < endIndex && endIndex <= terminalIndex) {
                    System.out.println("MAKAR " + "노선도 인덱스 확인: " + timeInfo.getMinute() + "분에 출발역(" + startIndex + ") 도착역(" + endIndex + ") 종착역(" + terminalIndex + ")");

                    canGoInSubway.set(true);
                    result.set(timeInfo);
                    task.complete(null);
                    return;
                }

                tasks.add(task);
                CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
                try {
                    allOf.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (canGoInSubway.get()) {
                    //현재 날짜
                    Calendar nowCalendar = Calendar.getInstance();
                    Calendar makarCalendar = Calendar.getInstance();

                    int makarHour = time.get(i).getIdx();
                    int makarMinute = result.get().getMinute();

                    int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
                    int nowMinute = nowCalendar.get(Calendar.MINUTE);

                    //막차시간이 시간표 상 24, 25인 경우
                    if (makarHour >= 24) {
                        makarHour -= 24;
                        //현재 시간이 오전 3시를 넘으면
                        if (nowHour >= 3) {
                            //하루를 더함
                            makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        //현재 시간이 오전 12시 ~ 오전 2시(새벽)면 하루를 안더함
                    }

                    makarCalendar.set(Calendar.HOUR_OF_DAY, makarHour);
                    makarCalendar.set(Calendar.MINUTE, makarMinute);
                    makarCalendar.set(Calendar.SECOND, 0);
                    makarCalendar.set(Calendar.MILLISECOND, 0);

                    //막차시간이 이미 지나간 경우 하루를 더함
                    if (nowCalendar.after(makarCalendar)) {
                        makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    return makarCalendar;
                }
            }
        }
        return null;
    }

    public Calendar computeTransferMakarTime(Calendar takingTime, int dayOfWeek, SubwaySchedule subwaySchedule,
                                             int odsayLaneType, int wayCode, int fromStationID, int toStationID) {

        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);

        //매시간마다
        for (int i = time.size() - 1; i >= 0; i--) {

            SubwaySchedule.OrdList.TimeDirection.TimeData timeData = time.get(i);

            //타야하는 시간보다 hour이 크면 skip
            if (isBeforeTakingHour(timeData, takingTime)) {
                continue;
            }

            String list = timeData.getList();
            List<TimeInfo> timeInfos = TimeInfo.parseTimeString(list);

            AtomicBoolean canGoInSubway = new AtomicBoolean(false);
            List<CompletableFuture<Void>> tasks = new ArrayList<>();

            AtomicReference<TimeInfo> result = new AtomicReference<>();
            //분마다
            for (int j = timeInfos.size() - 1; j >= 0; j--) {
                TimeInfo timeInfo = timeInfos.get(j);

                if (isBeforeTakingTime(timeData, timeInfo, takingTime)) {
                    continue;
                }

                //해당 호선의 지하철 노선도 데이터 가져오기
                CompletableFuture<Void> task = new CompletableFuture<>();
                LineMap lineMap = lineMapRepository.findByLineNum(odsayLaneType)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_LINE_MAP));


                int startIndex = -1;
                int endIndex = -1;
                int terminalIndex = -1;

                List<LineStation> stationList = new ArrayList<>();
                if (wayCode == UPTOWN) {
                    stationList = lineMap.getUpLineList();
                } else if (wayCode == DOWNTOWN) {
                    stationList = lineMap.getDownLineList();
                } else {
                    throw new GeneralException(ErrorStatus.NOT_FOUND_LINE_STATION);
                }

                //순회하면서 출발역, 도착역, 종착역의 index구하기
                for (int k = 0; k < stationList.size(); k++) {
                    LineStation lineStation = stationList.get(k);
                    String stationName = lineStation.getStationName();
                    if (stationName.equals(timeInfo.getTerminalStation())) {
                        terminalIndex = k;
                    }

                    int odsayStationID = lineStation.getOdsayStationId();
                    if (odsayStationID == fromStationID && startIndex == -1) {
                        startIndex = k;
                    }

                    //순환 경로인 경우 도착역은 뒤의 인덱스로 바뀌게
                    if (odsayStationID == toStationID) {
                        endIndex = k;
                    }
                }

                //index가 출발역, 도착역, 종착역순이면 해당 열차를 탈 수 있다.
                if (startIndex < endIndex && endIndex <= terminalIndex) {
                    System.out.println("MAKAR" + "노선도 인덱스 확인: " + timeInfo.getMinute() + "분에 출발역(" + startIndex + ") 도착역(" + endIndex + ") 종착역(" + terminalIndex + ")");
                    canGoInSubway.set(true);
                    result.set(timeInfo);
                    task.complete(null);
                    return;
                }

                tasks.add(task);
                CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
                try {
                    allOf.get();
                } catch (InterruptedException |
                         ExecutionException e) {
                    e.printStackTrace();
                }

                if (canGoInSubway.get()) {
                    //현재 날짜
                    Calendar nowCalendar = Calendar.getInstance();
                    Calendar makarCalendar = Calendar.getInstance();

                    int makarHour = time.get(i).getIdx();
                    int makarMinute = result.get().getMinute();

                    int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
                    int nowMinute = nowCalendar.get(Calendar.MINUTE);

                    //막차시간이 시간표 상 24, 25인 경우
                    if (makarHour >= 24) {
                        makarHour -= 24;
                        //현재 시간이 오전 3시를 넘으면
                        if (nowHour >= 3) {
                            //하루를 더함
                            makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        //현재 시간이 오전 12시 ~ 오전 2시면 하루를 안더함
                    }

                    makarCalendar.set(Calendar.HOUR_OF_DAY, makarHour);
                    makarCalendar.set(Calendar.MINUTE, makarMinute);
                    makarCalendar.set(Calendar.SECOND, 0);
                    makarCalendar.set(Calendar.MILLISECOND, 0);

                    //막차시간이 이미 지나간 경우 하루를 더함
                    if (nowCalendar.after(makarCalendar)) {
                        makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    return makarCalendar;
                }
            }
        }
        return null;
    }

    //요일에 맞는 시간표 가져오기
    private SubwaySchedule.OrdList getOrdListByDayOfWeek(int dayOfWeek, SubwaySchedule subwaySchedule) {
        if (dayOfWeek == Calendar.SATURDAY) {
            return subwaySchedule.getSatList();
        } else if (dayOfWeek == Calendar.SUNDAY) {
            return subwaySchedule.getSunList();
        } else {
            return subwaySchedule.getOrdList();
        }
    }

    //지하철 방면에 맞는 시간표 가져오기
    private List<SubwaySchedule.OrdList.TimeDirection.TimeData> getTimeByWayCode(int wayCode, SubwaySchedule.
            OrdList ordList) {
        if (wayCode == UPTOWN) {
            return ordList.getUp().getTime();
        } else if (wayCode == DOWNTOWN) {
            return ordList.getDown().getTime();
        } else {
            throw new IllegalArgumentException("Invalid wayCode: " + wayCode);
        }
    }

    private int getTakingHour(Calendar takingTime) {
        int takingHour = takingTime.get(Calendar.HOUR_OF_DAY);
        if (takingTime.get(Calendar.HOUR_OF_DAY) == 0) {
            takingHour = 24;
        } else if (takingTime.get(Calendar.HOUR_OF_DAY) == 1) {
            takingHour = 25;
        }
        return takingHour;
    }

    private boolean isBeforeTakingHour(SubwaySchedule.OrdList.TimeDirection.TimeData timeData, Calendar takingTime) {
        return timeData.getIdx() > getTakingHour(takingTime);
    }

    private boolean isBeforeTakingTime(SubwaySchedule.OrdList.TimeDirection.TimeData timeData, TimeInfo
            timeInfo, Calendar takingTime) {
        return (timeData.getIdx() == getTakingHour(takingTime) && timeInfo.getMinute() > takingTime.get(Calendar.MINUTE));
    }
}
