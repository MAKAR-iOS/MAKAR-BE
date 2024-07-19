package makar.dev.manager;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.converter.RouteConverter;
import makar.dev.converter.ScheduleConverter;
import makar.dev.domain.*;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.repository.RouteRepository;
import makar.dev.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteManager {
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final APIManager apiManager;

    // 경로 리스트 구하기
    public List<Route> getRoutes(Station sourceStation, Station destinationStation) throws IOException {
        RouteSearchResponse routeSearchResponse = apiManager.requestRoute(sourceStation.getX(), sourceStation.getY(), destinationStation.getX(), destinationStation.getY());
        List<Route> routes = new ArrayList<>();
        List<RouteSearchResponse.Path> paths = routeSearchResponse.getResult().getPath();

        //검색된 여러 경로 탐색
        for (RouteSearchResponse.Path path : paths) {
            RouteSearchResponse.Info pathInfo = path.getInfo();

            //1~9호선이 아닌 경로가 포함되어있는 경우 경로에서 제외
            if (isNotSubwayLineOneToNine(pathInfo.getMabObj())) {
                continue;
            }

            List<SubRoute> subRouteList = createSubRoutes(path);
            Schedule schedule = createSchedule(subRouteList);

            // route 생성
            Route route = RouteConverter.toRoute(sourceStation, destinationStation, schedule, pathInfo.getSubwayTransitCount(), subRouteList);
            routeRepository.save(route);

            // schedule 연관관계 설정
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

    private Schedule createSchedule(List<SubRoute> subRouteList) {
        //환승소요시간을 포함해서 전체소요시간 구하기
        int totalTime = getTransferTimeInRoute(subRouteList);

        //막차시간 구하기
        String sourceTime = getMakarTimeInRoutes(subRouteList);

        Schedule schedule = ScheduleConverter.toSchedule(sourceTime, "", totalTime);
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
        int transitCount = subRouteList.size() - 1;

        for (int i = 0; i < transitCount; i++) {
            SubRoute subRoute = subRouteList.get(i);
            totalTime += subRoute.getSectionTime();

            if (i + 1 < transitCount) {
                SubRoute nextSubRoute = subRouteList.get(i + 1);
                CompletableFuture<Transfer> transferInfoFuture = transferManager.searchTransferInfoAsync(subRoute, nextSubRoute);

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

    //막차시간 구하기
    private String getMakarTimeInRoutes(List<Route> routes) throws IOException, ExecutionException, InterruptedException {
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            Calendar takingTime = makarManager.computeMakarTime(route, dayOfWeek);
            return takingTime.getTime().toString();
        }
    }
}
