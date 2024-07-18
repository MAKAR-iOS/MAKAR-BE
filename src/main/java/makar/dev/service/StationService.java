package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.converter.StationConverter;
import makar.dev.domain.Station;
import makar.dev.dto.response.StationResponse;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public StationResponse.SearchDto searchStation(String param){
        List<Station> stationList = stationRepository.findByStationNameContaining(param);
        return StationConverter.toSearchDto(stationList);
    }


}
