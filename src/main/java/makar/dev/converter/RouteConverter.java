package makar.dev.converter;

import makar.dev.domain.Route;
import makar.dev.domain.Station;
import makar.dev.dto.response.RouteResponse;

public class RouteConverter {

    public static Route toRoute(Station sourceStation, Station destinationStation, int transferCount) {
        return Route.builder()
                .sourceStation(sourceStation)
                .destinationStation(destinationStation)
                .transferCount(transferCount)
                .build();
    }

    public static RouteResponse.RouteDto toRouteDto() {
        return RouteResponse.RouteDto.builder()
                .build();
    }

    public static RouteResponse.SearchRouteDto toSearchRouteDto() {
        return RouteResponse.SearchRouteDto.builder()
                .build();
    }

}
