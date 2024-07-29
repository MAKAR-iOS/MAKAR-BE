package makar.dev.converter;

import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.dto.response.NotiResponse;
import makar.dev.dto.response.RouteResponse;
import makar.dev.dto.response.UserResponse;

import java.util.List;

public class UserConverter {

    public static UserResponse.HomeDto toHomeDto(boolean isRouteSet, List<Noti> notiList,
                                                 List<Route> favoriteRouteList, List<Route> recentRouteList){
        List<RouteResponse.BriefRouteDto> favoriteRouteDtoList = favoriteRouteList.stream()
                .map(RouteConverter::toBriefRouteDto)
                .toList();
        List<RouteResponse.BriefRouteDto> recentRouteDtoList = recentRouteList.stream()
                .map(RouteConverter::toBriefRouteDto)
                .toList();

        if (notiList.isEmpty())
            return UserResponse.HomeDto.builder()
                    .isRouteSet(isRouteSet)
                    .sourceStationName(null)
                    .destinationStationName(null)
                    .makarTime(null)
                    .getoffTime(null)
                    .notiList(null)
                    .favoriteRouteList(favoriteRouteDtoList)
                    .recentRouteList(recentRouteDtoList)
                    .build();

        Route route = notiList.get(0).getRoute();
        List<NotiResponse.NotiDto> notiDtoList = notiList.stream()
                .map(NotiConverter::toNotiDto)
                .toList();
        return UserResponse.HomeDto.builder()
                .isRouteSet(isRouteSet)
                .sourceStationName(route.getSourceStation().getStationName())
                .destinationStationName(route.getDestinationStation().getStationName())
                .makarTime(route.getSchedule().getSourceTime())
                .getoffTime(route.getSchedule().getDestinationTime())
                .notiList(notiDtoList)
                .favoriteRouteList(favoriteRouteDtoList)
                .recentRouteList(recentRouteDtoList)
                .build();
    }
}
