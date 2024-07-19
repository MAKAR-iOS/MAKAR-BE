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

    // 환승이 없는 경우 막차 시간 구하기
    public Calendar computeLastMakarTime(int dayOfWeek, SubwaySchedule subwaySchedule, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);
        return findMakarTime(time, null, odsayLaneType, wayCode, fromStationID, toStationID);
    }

    // 환승이 있는 경우 막차 시간 구하기
    public Calendar computeTransferMakarTime(Calendar takingTime, int dayOfWeek, SubwaySchedule subwaySchedule, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        SubwaySchedule.OrdList ordList = getOrdListByDayOfWeek(dayOfWeek, subwaySchedule);
        List<SubwaySchedule.OrdList.TimeDirection.TimeData> time = getTimeByWayCode(wayCode, ordList);
        return findMakarTime(time, takingTime, odsayLaneType, wayCode, fromStationID, toStationID);
    }

    private Calendar findMakarTime(List<SubwaySchedule.OrdList.TimeDirection.TimeData> time, Calendar takingTime, int odsayLaneType, int wayCode, int fromStationID, int toStationID) {
        for (int i = time.size() - 1; i >= 0; i--) {
            SubwaySchedule.OrdList.TimeDirection.TimeData timeData = time.get(i);

            if (takingTime != null && isBeforeTakingHour(timeData, takingTime)) {
                continue;
            }

            List<TimeInfo> timeInfos = TimeInfo.parseTimeString(timeData.getList());
            AtomicBoolean canGoInSubway = new AtomicBoolean(false);
            AtomicReference<TimeInfo> result = new AtomicReference<>();
            List<CompletableFuture<Void>> tasks = new ArrayList<>();

            for (int j = timeInfos.size() - 1; j >= 0; j--) {
                TimeInfo timeInfo = timeInfos.get(j);

                if (takingTime != null && isBeforeTakingTime(timeData, timeInfo, takingTime)) {
                    continue;
                }

                CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                    try {
                        LineMap lineMap = lineMapRepository.findByLineNum(odsayLaneType)
                                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_LINE_MAP));

                        List<LineStation> stationList = getStationListByWayCode(lineMap, wayCode);
                        int startIndex = getStationIndex(stationList, fromStationID);
                        int endIndex = getStationIndex(stationList, toStationID);
                        int terminalIndex = getTerminalIndex(stationList, timeInfo.getTerminalStation());

                        if (startIndex < endIndex && endIndex <= terminalIndex) {
                            System.out.println("MAKAR: 노선도 인덱스 확인: " + timeInfo.getMinute() + "분에 출발역(" + startIndex + ") 도착역(" + endIndex + ") 종착역(" + terminalIndex + ")");
                            canGoInSubway.set(true);
                            result.set(timeInfo);
                        }
                    } catch (GeneralException e) {
                        e.printStackTrace();
                    }
                });

                tasks.add(task);
            }

            waitForTasks(tasks);

            if (canGoInSubway.get()) {
                return computeMakarCalendar(timeData.getIdx(), result.get().getMinute());
            }
        }
        return null;
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

    private void waitForTasks(List<CompletableFuture<Void>> tasks) {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
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
