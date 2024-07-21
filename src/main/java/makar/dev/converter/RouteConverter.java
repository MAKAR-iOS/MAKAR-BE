package makar.dev.converter;

import makar.dev.domain.*;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.dto.response.RouteResponse;

import java.util.List;

public class RouteConverter {

    public static Route toRoute(Station sourceStation, Station destinationStation, Schedule schedule, int transferCount, List<SubRoute> subRouteList) {
        return Route.builder()
                .sourceStation(sourceStation)
                .destinationStation(destinationStation)
                .schedule(schedule)
                .transferCount(transferCount)
                .subRouteList(subRouteList)
                .build();
    }

    public static RouteResponse.RouteDto toRouteDto(Route route) {
        List<RouteResponse.SubRouteDto> subRouteDtoList = route.getSubRouteList().stream()
                .map(RouteConverter::toSubRouteDto)
                .toList();

        return RouteResponse.RouteDto.builder()
                .routeId(route.getRouteId())
                .sourceStationName(route.getSourceStation().getStationName())
                .sourceLineNum(route.getSourceStation().getOdsayLaneType())
                .destinationStationName(route.getDestinationStation().getStationName())
                .destinationLineNum(route.getDestinationStation().getOdsayLaneType())
                .sourceTime(route.getSchedule().getSourceTime())
                .destinationTime(route.getSchedule().getDestinationTime())
                .totalTime(route.getSchedule().getTotalTime())
                .subRouteDtoList(subRouteDtoList)
                .build();
    }

    public static SubRoute toSubRoute(RouteSearchResponse.SubPath subPath) {
        RouteSearchResponse.Lane lane = subPath.getLane().get(0);

        return SubRoute.builder()
                .toStationName(subPath.getEndStationName())
                .fromStationName(subPath.getStartStationName())
                .toStationCode(subPath.getEndID())
                .fromStationCode(subPath.getStartID())
                .lineNum(lane.getLineNum())
                .wayCode(subPath.getWayCode())
                .sectionTime(subPath.getSectionTime())
                .build();
    }

    public static RouteResponse.SubRouteDto toSubRouteDto(SubRoute subRoute) {
        return RouteResponse.SubRouteDto.builder()
                .fromStationName(subRoute.getFromStationName())
                .toStationName(subRoute.getToStationName())
                .lineNum(subRoute.getLineNum())
                .sectionTime(subRoute.getSectionTime())
                .transferTime(subRoute.getTransferTime())
                .build();
    }

    public static RouteResponse.SearchRouteDto toSearchRouteDto(List<Route> routeList) {
        List<RouteResponse.RouteDto> routeDtoList = routeList.stream()
                .map(RouteConverter::toRouteDto)
                .toList();

        return RouteResponse.SearchRouteDto.builder()
                .routeDtoList(routeDtoList)
                .build();
    }

    public static RouteResponse.SetRouteDto toSetRouteDto(Route route, Noti makarNoti, Noti getOffNoti){
        List<RouteResponse.SubRouteDto> subRouteDtoList = route.getSubRouteList().stream()
                .map(RouteConverter::toSubRouteDto)
                .toList();
        return RouteResponse.SetRouteDto.builder()
                .routeId(route.getRouteId())
                .sourceStationName(route.getSourceStation().getStationName())
                .sourceLineNum(route.getSourceStation().getOdsayLaneType())
                .destinationStationName(route.getDestinationStation().getStationName())
                .destinationLineNum(route.getDestinationStation().getOdsayLaneType())
                .sourceTime(route.getSchedule().getSourceTime())
                .destinationTime(route.getSchedule().getDestinationTime())
                .totalTime(route.getSchedule().getTotalTime())
                .subRouteDtoList(subRouteDtoList)
                .getOffMinute(makarNoti.getNoti_minute())
                .getOffMinute(getOffNoti.getNoti_minute())
                .build();
    }

}
