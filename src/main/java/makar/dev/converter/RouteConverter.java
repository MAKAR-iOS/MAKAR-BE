package makar.dev.converter;

import makar.dev.domain.*;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.dto.response.RouteResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteConverter {

    public static Route toRoute(Station sourceStation, Station destinationStation, Schedule schedule, int transferCount, List<SubRoute> subRouteList) {
        return Route.builder()
                .sourceStation(sourceStation)
                .destinationStation(destinationStation)
                .schedule(schedule)
                .transferCount(transferCount-1)
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
                .sourceLineNum(route.getSourceStation().getLineNum())
                .destinationStationName(route.getDestinationStation().getStationName())
                .destinationLineNum(route.getDestinationStation().getLineNum())
                .sourceTime(route.getSchedule().getSourceTime())
                .destinationTime(route.getSchedule().getDestinationTime())
                .totalTime(route.getSchedule().getTotalTime())
                .transferCount(route.getTransferCount())
                .subRouteDtoList(subRouteDtoList)
                .build();
    }

    public static RouteResponse.RouteDetailDto toRouteDetailDto(Route route) {
        List<RouteResponse.SubRouteDetailDto> subRouteDetailDtoList = route.getSubRouteList().stream()
                .map(RouteConverter::toSubRouteDetailDto)
                .toList();

        return RouteResponse.RouteDetailDto.builder()
                .routeId(route.getRouteId())
                .sourceStationName(route.getSourceStation().getStationName())
                .sourceLineNum(route.getSourceStation().getLineNum())
                .destinationStationName(route.getDestinationStation().getStationName())
                .destinationLineNum(route.getDestinationStation().getLineNum())
                .sourceTime(route.getSchedule().getSourceTime())
                .destinationTime(route.getSchedule().getDestinationTime())
                .totalTime(route.getSchedule().getTotalTime())
                .transferCount(route.getTransferCount())
                .subRouteDtoList(subRouteDetailDtoList)
                .build();
    }

    public static RouteResponse.BriefRouteDto toBriefRouteDto(Route route){
        return RouteResponse.BriefRouteDto.builder()
                .sourceStationName(route.getSourceStation().getStationName())
                .sourceLineNum(route.getSourceStation().getLineNum())
                .destinationStationName(route.getDestinationStation().getStationName())
                .destinationLineNum(route.getDestinationStation().getLineNum())
                .build();
    }

    public static List<RouteResponse.BriefRouteDtoWithRouteId> toBriefRouteDtoList(List<Route> routes){
        return routes.stream()
                .map(route ->
                    RouteResponse.BriefRouteDtoWithRouteId.builder()
                        .routeId(route.getRouteId())
                        .sourceStationName(route.getSourceStation().getStationName())
                        .sourceLineNum(route.getSourceStation().getLineNum())
                        .destinationStationName(route.getDestinationStation().getStationName())
                        .destinationLineNum(route.getDestinationStation().getLineNum())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static SubRoute toSubRoute(RouteSearchResponse.SubPath subPath) {
        RouteSearchResponse.Lane lane = subPath.getLane().get(0);

        // parse path list
        RouteSearchResponse.PassStopList passStopList = subPath.getPassStopList();
        List<RouteSearchResponse.Station> stations = passStopList.getStations();
        List<String> path = new ArrayList<>();
        for (RouteSearchResponse.Station station : stations){
            path.add(station.getStationName());
        }

        return SubRoute.builder()
                .toStationName(subPath.getEndStationName())
                .fromStationName(subPath.getStartStationName())
                .toStationCode(subPath.getEndID())
                .fromStationCode(subPath.getStartID())
                .lineNum(lane.getLineNum())
                .wayCode(subPath.getWayCode())
                .sectionTime(subPath.getSectionTime())
                .path(path)
                .build();
    }

    public static RouteResponse.SubRouteDto toSubRouteDto(SubRoute subRoute) {
        String lineNum = mapOdsayStationTypeToLineNum(subRoute.getLineNum());
        return RouteResponse.SubRouteDto.builder()
                .fromStationName(subRoute.getFromStationName())
                .toStationName(subRoute.getToStationName())
                .lineNum(lineNum)
                .sectionTime(subRoute.getSectionTime())
                .transferTime(subRoute.getTransferTime())
                .build();
    }

    public static RouteResponse.SubRouteDetailDto toSubRouteDetailDto(SubRoute subRoute) {
        String lineNum = mapOdsayStationTypeToLineNum(subRoute.getLineNum());
        return RouteResponse.SubRouteDetailDto.builder()
                .fromStationName(subRoute.getFromStationName())
                .toStationName(subRoute.getToStationName())
                .lineNum(lineNum)
                .sectionTime(subRoute.getSectionTime())
                .transferTime(subRoute.getTransferTime())
                .path(subRoute.getPath())
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

    private static String mapOdsayStationTypeToLineNum(int stationType) {
        if (stationType >= 1 && stationType <= 9) {
            return stationType + "호선";
        }
        if (stationType == 101) {
            return "공항철도";
        }
        if (stationType == 104) {
            return "경의중앙";
        }
        return null;
    }
}
