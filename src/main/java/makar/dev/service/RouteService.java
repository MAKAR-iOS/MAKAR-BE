package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.RouteConverter;
import makar.dev.domain.Route;
import makar.dev.domain.Station;
import makar.dev.dto.request.RouteRequest;
import makar.dev.dto.response.RouteResponse;
import makar.dev.manager.RouteManager;
import makar.dev.repository.RouteRepository;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final RouteManager routeManager;

    // 경로 리스트 검색
    public RouteResponse.SearchRouteDto searchRoute(RouteRequest.SearchRouteDto searchRouteDto) throws IOException {
        Station sourceStation = findStation(searchRouteDto.getSourceStationName(), searchRouteDto.getSourceLineNum());
        Station destinationStation = findStation(searchRouteDto.getDestinationStationName(), searchRouteDto.getDestinationLineNum());

        // search route
        List<Route> routeList = new ArrayList<>();
        routeList= routeManager.getRoutes(sourceStation, destinationStation);

        return RouteConverter.toSearchRouteDto(routeList);
    }

    private Station findStation(String name, int num){
        return stationRepository.findByStationNameAndOdsayLaneType(name, num)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }

}
