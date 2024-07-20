package makar.dev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.StationConverter;
import makar.dev.domain.Station;
import makar.dev.domain.data.OdsayStation;
import makar.dev.dto.response.StationResponse;
import makar.dev.manager.APIManager;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final APIManager apiManager;

    // 역 검색
    public StationResponse.SearchDto searchStation(String param) {
        List<Station> stationList = stationRepository.findByStationNameContaining(param);
        return StationConverter.toSearchDto(stationList);
    }

    // 역 세부 정보 조회
    public StationResponse.StationDetailDto getStationDetail(String stationName, String lineNum) {
        List<Station> station = stationRepository.findByStationNameAndLineNum(stationName, lineNum);
        return StationConverter.toStationDetailDto(station.get(0));
    }

    // 역 세부 정보 수정
    @Transactional
    public StationResponse.StationDetailDto updateStationDetail(String stationName, String lineNum) {
        Station station = stationRepository.findByStationNameAndLineNum(stationName, lineNum).get(0);

        // 대중교통 정류장 검색 API 호출
        List<OdsayStation.Station> stations = apiManager.searchStation(stationName);

        if (stations.isEmpty()) {
            throw new GeneralException(ErrorStatus.NOT_FOUND_STATION);
        }

        // Station field update
        for (OdsayStation.Station odsayStation : stations) {
            int findStationType = odsayStation.getType();
            String findLineNum = mapOdsayStationTypeToLineNum(findStationType);

            if (findLineNum.equals(lineNum)) {
                updateOdsayStationDataInEntity(station, odsayStation);
            }
        }
        return StationConverter.toStationDetailDto(station);
    }

    private String mapOdsayStationTypeToLineNum(int stationType) {
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

    private void updateOdsayStationDataInEntity(Station station, OdsayStation.Station odsayStation) {
        station.setOdsayStationID(odsayStation.getStationID());
        station.setX(odsayStation.getX());
        station.setY(odsayStation.getY());
        station.setOdsayLaneType(odsayStation.getType());
    }


}
