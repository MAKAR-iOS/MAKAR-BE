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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@EnableAsync
@Service
@RequiredArgsConstructor
public class MakarManager {
    AtomicBoolean canGoInSubway = new AtomicBoolean(false);
    AtomicReference<TimeInfo> resultTimeInfo = new AtomicReference<>();
    AtomicReference<Calendar> resultCalender = new AtomicReference<>();


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
                computeLastMakarTime(dayOfWeek, subwaySchedule, lastSubRoute.getLineNum(), lastSubRoute.getWayCode(), lastSubRoute.getFromStationCode(), lastSubRoute.getToStationCode());
                takingTime = resultCalender.get();
            } else {
                int sectionTime = lastSubRoute.getSectionTime();
                sectionTime += lastSubRoute.getTransferTime();
                takingTime.add(Calendar.MINUTE, -sectionTime);

                // 환승시간을 포함하여 이후 서브 경로의 막차 시간 구하기
                computeTransferMakarTime(takingTime, dayOfWeek, subwaySchedule, lastSubRoute.getLineNum(), lastSubRoute.getWayCode(), lastSubRoute.getFromStationCode(), lastSubRoute.getToStationCode());
                takingTime = resultCalender.get();
            }
            makarTimes.add(0, takingTime.getTime());
        }
        return takingTime;
    }

    // 환승이 없는 경우 막차 시간 구하기
    private void computeLastMakarTime(int dayOfWeek, SubwaySchedule subwaySchedule, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);
        findMakarTime(time, null, odsayLaneType, wayCode, fromStationID, toStationID);
    }

    // 환승이 있는 경우 막차 시간 구하기
    private void computeTransferMakarTime(Calendar takingTime, int dayOfWeek, SubwaySchedule subwaySchedule, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);
        System.out.println("[WayCode] : "+wayCode);
        for (SubwaySchedule.OrdList.TimeDirection.TimeData timeData : time){
            System.out.println("Time : "+timeData.toString());
        }
        findMakarTime(time, takingTime, odsayLaneType, wayCode, fromStationID, toStationID);
    }

    @Transactional
    public void findMakarTime(List<SubwaySchedule.OrdList.TimeDirection.TimeData> time, Calendar takingTime, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        for (int i = time.size() - 1; i >= 0; i--) {
            if (canGoInSubway.get())
                break;

            SubwaySchedule.OrdList.TimeDirection.TimeData timeData = time.get(i);

            if (takingTime != null && isBeforeTakingHour(timeData, takingTime))
                continue;

            List<TimeInfo> timeInfos = TimeInfo.parseTimeString(timeData.getList());

            for (int j = timeInfos.size() - 1; j >= 0; j--) {
                TimeInfo timeInfo = timeInfos.get(j);

                if (takingTime != null && isBeforeTakingTime(timeData, timeInfo, takingTime))
                    continue;

                List<LineMap> lineMapList = lineMapRepository.findByLineNum(odsayLaneType);
                CompletableFuture<Void> task = new CompletableFuture<>();

                for (LineMap lineMap : lineMapList) {
                    if (canGoInSubway.get())
                        break;

                    // TODO: 상행 리스트가 안가져와짐
                    List<LineStation> stationList = getStationListByWayCode(lineMap, wayCode);
                    System.out.println("[WayCode] : "+wayCode);
                    for (LineStation lineStation : stationList){
                        System.out.println("[LineStation] : "+lineStation.getStationName());
                    }

                    int startIndex = getStationIndex(stationList, fromStationID);
                    int endIndex = getStationIndex(stationList, toStationID);
                    int terminalIndex = getTerminalIndex(stationList, timeInfo.getTerminalStation());

                    System.out.println("TimeInfo: 노선도 인덱스 확인: " + timeInfo.getMinute() + "분에 출발역(" + startIndex + ") 도착역(" + endIndex + ") 종착역(" + terminalIndex + ")");

                    if (startIndex < endIndex && endIndex <= terminalIndex) {
                        canGoInSubway.set(true);
                        resultTimeInfo.set(timeInfo);
                        task.complete(null);
                        break;
                    }
                }
                task.complete(null);
                tasks.add(task);
            }

            waitForTasks(tasks, canGoInSubway);

            if (canGoInSubway.get()) {
                resultCalender.set(computeMakarCalendar(timeData.getIdx(), resultTimeInfo.get().getMinute()));
            }
        }
    }


    private List<LineStation> getStationListByWayCode(LineMap lineMap, int wayCode) {
        if (wayCode == UPTOWN) {
            return lineMap.getUpLineList();
        } else if (wayCode == DOWNTOWN) {
            return lineMap.getDownLineList();
        } else {
            throw new GeneralException(ErrorStatus.NOT_FOUND_LINE_STATION);
        }
    }

    private int getStationIndex(List<LineStation> stationList, int stationID) {
        for (int k = 0; k < stationList.size(); k++) {
            if (stationList.get(k).getOdsayStationId() == stationID) {
                return k;
            }
        }
        return -1;
    }

    private int getTerminalIndex(List<LineStation> stationList, String terminalStation) {
        for (int k = 0; k < stationList.size(); k++) {
            if (stationList.get(k).getStationName().equals(terminalStation)) {
                return k;
            }
        }
        return -1;
    }

    private void waitForTasks(List<CompletableFuture<Void>> tasks, AtomicBoolean canGoInSubway) {
        for (CompletableFuture<Void> task : tasks) {
            if (canGoInSubway.get()) {
                break;
            }
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Calendar computeMakarCalendar(int makarHour, int makarMinute) {
        Calendar nowCalendar = Calendar.getInstance();
        Calendar makarCalendar = Calendar.getInstance();

        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);

        if (makarHour >= 24) {
            makarHour -= 24;
            if (nowHour >= 3) {
                makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        makarCalendar.set(Calendar.HOUR_OF_DAY, makarHour);
        makarCalendar.set(Calendar.MINUTE, makarMinute);
        makarCalendar.set(Calendar.SECOND, 0);
        makarCalendar.set(Calendar.MILLISECOND, 0);

        if (nowCalendar.after(makarCalendar)) {
            makarCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return makarCalendar;
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
            throw new GeneralException(ErrorStatus.NOT_FOUND_LINE_MAP);
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
