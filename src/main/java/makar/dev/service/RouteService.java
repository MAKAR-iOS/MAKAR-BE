package makar.dev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.RouteConverter;
import makar.dev.converter.ScheduleConverter;
import makar.dev.domain.*;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.dto.request.RouteRequest;
import makar.dev.dto.response.RouteResponse;
import makar.dev.manager.APIManager;
import makar.dev.manager.MakarManager;
import makar.dev.repository.RouteRepository;
import makar.dev.repository.ScheduleRepository;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final ScheduleRepository scheduleRepository;
    private final APIManager apiManager;
    private final MakarManager makarManager;
    private final TransferService transferService;

    // 경로 리스트 검색
    public RouteResponse.SearchRouteDto searchRoute(RouteRequest.SearchRouteDto searchRouteDto) {
        Station sourceStation = findStation(searchRouteDto.getSourceStationName(), searchRouteDto.getSourceLineNum());
        Station destinationStation = findStation(searchRouteDto.getDestinationStationName(), searchRouteDto.getDestinationLineNum());

        // search route
        List<Route> routeList = getRoutes(sourceStation, destinationStation);
        return RouteConverter.toSearchRouteDto(routeList);
    }

    private Station findStation(String name, int num){
        return stationRepository.findByStationNameAndOdsayLaneType(name, num)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }

    // 경로 리스트 구하기
    private List<Route> getRoutes(Station sourceStation, Station destinationStation) {
        List<Route> routeList = routeRepository.findBySourceStationAndDestinationStation(sourceStation, destinationStation);

        // 경로 table이 있는 경우
        if (!routeList.isEmpty()) {
            updateSchedules(routeList);
            return routeList;
        }

        return fetchAndSaveRoutesFromAPI(sourceStation, destinationStation);
    }

    private void updateSchedules(List<Route> routeList) {
        for (Route route : routeList) {
            Schedule schedule = createSchedule(route.getSubRouteList());
            scheduleRepository.delete(route.getSchedule());
            route.updateSchedule(schedule);
            schedule.setRoute(route);
        }
    }

    private List<Route> fetchAndSaveRoutesFromAPI(Station sourceStation, Station destinationStation) {
        RouteSearchResponse routeSearchResponse = apiManager.requestRoute(sourceStation.getX(), sourceStation.getY(), destinationStation.getX(), destinationStation.getY());
        List<Route> routes = new ArrayList<>();
        List<RouteSearchResponse.Path> paths = routeSearchResponse.getResult().getPath();

        //검색된 여러 경로 탐색
        for (RouteSearchResponse.Path path : paths) {
            RouteSearchResponse.Info pathInfo = path.getInfo();

            //1~9호선이 아닌 경로가 포함되어있는 경우 경로에서 제외
            if (isNotSubwayLineOneToNine(pathInfo.getMabObj())) continue;

            List<SubRoute> subRouteList = createSubRoutes(path);
            Schedule schedule = createSchedule(subRouteList);

            // route 생성
            Route route = RouteConverter.toRoute(sourceStation, destinationStation, schedule, path.getInfo().getSubwayTransitCount(), subRouteList);
            routeRepository.save(route);
            schedule.setRoute(route);
            routes.add(route);
        }

        return routes;
    }

    private List<SubRoute> createSubRoutes(RouteSearchResponse.Path path) {
        List<SubRoute> subRouteList = new ArrayList<>();
        List<RouteSearchResponse.SubPath> subPaths = path.getSubPath();

        for (RouteSearchResponse.SubPath subPath : subPaths) {
            if (subPath.isWalkType()) {
                continue;
            }

            SubRoute subRoute = RouteConverter.toSubRoute(subPath);
            subRouteList.add(subRoute);
        }

        return subRouteList;
    }

    @Transactional
    public Schedule createSchedule(List<SubRoute> subRouteList) {
        //환승소요시간을 포함해서 전체소요시간 구하기
        int totalTime = getTransferTimeInRoute(subRouteList);

        //막차시간 구하기
        String sourceTime = getMakarTime(subRouteList);
        String destinationTime = getDestinationTime(sourceTime, totalTime);

        Schedule schedule = ScheduleConverter.toSchedule(sourceTime, destinationTime, totalTime);
        scheduleRepository.save(schedule);
        return schedule;
    }

    private boolean isNotSubwayLineOneToNine(String input) {

        // 정규식을 사용하여 숫자 1~9인지 확인
        Pattern pattern = Pattern.compile("^[1-9]$");

        // @로 섹션 나누기
        StringTokenizer sectionsTokenizer = new StringTokenizer(input, "@");
        while (sectionsTokenizer.hasMoreTokens()) {
            String section = sectionsTokenizer.nextToken();

            // :로 값을 나누기
            StringTokenizer valuesTokenizer = new StringTokenizer(section, ":");
            if (valuesTokenizer.hasMoreTokens()) {
                String firstValue = valuesTokenizer.nextToken();

                // 정규식을 사용하여 숫자 1~9인지 확인
                Matcher matcher = pattern.matcher(firstValue);
                if (!matcher.matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    //환승소요시간을 포함해서 전체소요시간 구하기
    private int getTransferTimeInRoute(List<SubRoute> subRouteList) {
        int totalTime = 0;
        int transitCount = subRouteList.size();

        for (int i = 0; i < transitCount; i++) {
            SubRoute subRoute = subRouteList.get(i);
            totalTime += subRoute.getSectionTime();

            if (i + 1 < transitCount) {
                SubRoute nextSubRoute = subRouteList.get(i + 1);
                CompletableFuture<Transfer> transferInfoFuture = transferService.searchTransferInfoAsync(subRoute, nextSubRoute);

                Transfer transfer;
                int transferTime = 0;
                try {
                    transfer = transferInfoFuture.get();
                    transferTime = transfer.getTransferTime();
                } catch (InterruptedException | ExecutionException e) {
                    // 에러 시 기본 환승 소요시간(0)
                }
                // subRoute 이후 환승 소요 시간 설정
                subRoute.setTransferTime(transferTime);
                // 전체 소요 시간
                totalTime += transferTime;
            }
        }
        return totalTime;
    }

    // 막차시간 구하기
    private String getMakarTime(List<SubRoute> subRouteList) {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            Calendar takingTime = makarManager.computeMakarTime(subRouteList, dayOfWeek);
            return takingTime.getTime().toString();
    }

    // 도착시간 구하기
    private String getDestinationTime(String sourceTime, int totalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        try {
            Date date = sdf.parse(sourceTime);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, totalTime); // totalTime을 분 단위로 더함
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_SOURCE_TIME_FORMAT);
        }
        return sdf.format(calendar.getTime());
    }

}
