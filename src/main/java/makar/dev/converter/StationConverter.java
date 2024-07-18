package makar.dev.converter;

import makar.dev.domain.Station;
import makar.dev.dto.response.StationResponse;

import java.util.List;

public class StationConverter {
    public static Station toStation(String stationName, String stationCode, String lineNum, String railOpr){
        return Station.builder()
                .stationName(stationName)
                .stationCode(stationCode)
                .lineNum(lineNum)
                .railOpr(railOpr)
                .build();
    }

    public static StationResponse.StationDto toStationDto(Station station){
        return StationResponse.StationDto.builder()
                .stationName(station.getStationName())
                .lineNum(station.getOdsayLaneType())
                .build();
    }

    public static StationResponse.SearchDto toSearchDto(List<Station> stationList){
        List<StationResponse.StationDto> stationDtoList = stationList.stream()
                .map(StationConverter::toStationDto)
                .toList();

        return StationResponse.SearchDto.builder()
                .stationDtoList(stationDtoList)
                .build();
    }
}
