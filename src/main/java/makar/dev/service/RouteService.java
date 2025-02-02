package makar.dev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.NotiConverter;
import makar.dev.converter.RouteConverter;
import makar.dev.converter.ScheduleConverter;
import makar.dev.domain.*;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.dto.response.RouteResponse;
import makar.dev.manager.APIManager;
import makar.dev.manager.MakarManager;
import makar.dev.repository.*;
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
    private final UserRepository userRepository;
    private final NotiRepository notiRepository;
    private final RecentRouteRepository recentRouteRepository;
    private final APIManager apiManager;
    private final MakarManager makarManager;
    private final TransferService transferService;

    // 경로 리스트 검색
    public RouteResponse.SearchRouteDto searchRoute(String fromStationName, String fromLineNum, String toStationName, String toLineNum) {
        Station sourceStation = findStation(fromStationName, fromLineNum);
        Station destinationStation = findStation(toStationName, toLineNum);

        // search route
        List<Route> routeList = getRoutes(sourceStation, destinationStation);
        return RouteConverter.toSearchRouteDto(routeList);
    }

    // 경로 설정
    @Transactional
    public RouteResponse.RouteDto setRoute(Long userId, Long routeId){
        User user = findUserById(userId);
        Route route = findRouteById(routeId);

        // 이미 경로가 지정되었을 경우
        if (!user.getNotiList().isEmpty()){
            throw new GeneralException(ErrorStatus.ALREADY_ROUTE_SET);
        }

        // 막차 Noti 생성
        Noti makarNoti = NotiConverter.toMAKARNoti(route, user, 10);
        // 하차 Noti 생성
        Noti getOffNoti = NotiConverter.toGetOffNoti(route, user, 10);

        notiRepository.save(makarNoti);
        notiRepository.save(getOffNoti);

        user.addNotiList(makarNoti);
        user.addNotiList(getOffNoti);

        // 해당 경로, 유저 정보를 갖는 RecentRoute 객체 생성 후 저장
        addRecentRoute(user, route);

        return RouteConverter.toRouteDto(route);
    }

    // 경로 삭제
    @Transactional
    public void deleteRoute(Long userId){
        User user = findUserById(userId);
        List<Noti> notiList = user.getNotiList();

        // 설정된 경로가 없을 경우
        if (notiList.isEmpty()){
            throw new GeneralException(ErrorStatus.INVALID_ROUTE_DELETE);
        }

        // noti list 초기화
        notiRepository.deleteAll(notiList);
        user.getNotiList().clear();
    }

    // 설정된 경로 조회
    public RouteResponse.RouteDetailDto getSetRoute(Long userId){
        User user = findUserById(userId);
        List<Noti> notiList = user.getNotiList();
        if (notiList.isEmpty())
            throw new GeneralException(ErrorStatus.INVALID_ROUTE_SET);

        Route route = notiList.get(0).getRoute();
        return RouteConverter.toRouteDetailDto(route);
    }

    // 즐겨찾는 경로 조회
    public List<RouteResponse.BriefRouteDto> getFavoriteRouteList(TokenDto tokenDto) {
        User user = findUserById(tokenDto.getUserId());
        List<Route> favoriteRouteList = user.getFavoriteRouteList();
        // TODO: fix routeId order
        return favoriteRouteList.stream()
                .map(RouteConverter::toBriefRouteDto)
                .toList();
    }

    // 즐겨찾는 경로 추가
    @Transactional
    public void addFavoriteRoute(TokenDto tokenDto, Long routeId) {
        User user = findUserById(tokenDto.getUserId());
        Route route = findRouteById(routeId);

        // 해당 경로가 이미 즐겨찾는 경로로 설정된 경우
        List<Route> favoriteRouteList = user.getFavoriteRouteList();
        if (favoriteRouteList.contains(route))
            throw new GeneralException(ErrorStatus.ALREADY_FAVORITE_ROUTE_SET);

        user.addFavoriteRoute(route);
    }

    // 즐겨찾는 경로 삭제
    @Transactional
    public List<RouteResponse.BriefRouteDto> deleteFavoriteRoute(TokenDto tokenDto, Long routeId) {
        User user = findUserById(tokenDto.getUserId());
        Route route = findRouteById(routeId);

        // 해당 경로가 즐겨찾는 경로로 설정되지 않은 경우
        List<Route> favoriteRouteList = user.getFavoriteRouteList();
        if (!favoriteRouteList.contains(route))
            throw new GeneralException(ErrorStatus.INVALID_FAVORITE_ROUTE_DELETE);

        favoriteRouteList.remove(route);
        return favoriteRouteList.stream()
                .map(RouteConverter::toBriefRouteDto)
                .toList();
    }

    private Station findStation(String stationName, String lineNum){
        return stationRepository.findByStationNameAndLineNum(stationName, lineNum)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));
    }

    private Route findRouteById(Long routeId){
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_ROUTE));
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
            Schedule newSchedule = createSchedule(route.getSubRouteList());

            Schedule oldSchedule = route.getSchedule();
            route.updateSchedule(newSchedule);
            newSchedule.setRoute(route);

            // 기존 스케쥴 삭제
            scheduleRepository.delete(oldSchedule);
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

    // 특정 경로, 유저를 기반으로 하는 RecentRoute 객체 생성 후 저장
    @Transactional
    public void addRecentRoute(User user, Route route) {
        // 동일한 routeId를 가진 Route가 이미 최근 경로 리스트에 존재한다면
        // DB와 리스트에서 제거
        recentRouteRepository.findByUserAndRoute(user, route).ifPresent(
                recentRoute -> {
                    recentRouteRepository.delete(recentRoute);
                    user.removeRecentRoute(recentRoute);
                }
        );

        // 최근 경로 리스트 사이즈 최대 5개 유지
        if (user.getRecentRouteList().size() >= 5) {
            RecentRoute recentRoute = user.getRecentRouteList().get(0);
            recentRouteRepository.delete(recentRoute);
        }

        // 새로운 RecentRoute 생성 및 저장
        RecentRoute newRecentRoute = new RecentRoute(user, route);
        recentRouteRepository.save(newRecentRoute);
    }

    // 최근 경로 리스트 조회하기
    public RouteResponse.RecentRouteListDto getAllRecentRoute(Long userId) {
        User user = findUserById(userId);
        return RouteResponse.RecentRouteListDto.builder()
                .recentRouteList(RouteConverter.toBriefRouteDtoList(user.findRecentRouteList()))
                .build();
    }

    // 특정 최근 경로 삭제
    @Transactional
    public void deleteRecentRoute(Long userId, Long routeId) {
        User user = findUserById(userId);
        Route route = findRouteById(routeId);
        RecentRoute recentRoute = recentRouteRepository.findByUserAndRoute(user, route).orElseThrow(
                () -> new GeneralException(ErrorStatus.NOT_FOUND_IN_RECENT_ROUTE_LIST)
        );
        recentRouteRepository.delete(recentRoute);
        user.removeRecentRoute(recentRoute);
    }

    // 모든 최근 경로 삭제
    @Transactional
    public void deleteAllRecentRoute(Long userId) {
        User user = findUserById(userId);
        recentRouteRepository.deleteByUser(user);
        user.clearRecentRouteList();
    }
}
