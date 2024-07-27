package makar.dev.converter;

import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
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
                .lineNum(station.getLineNum())
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

    public static StationResponse.StationDetailDto toStationDetailDto(Station station){
        try {
            return StationResponse.StationDetailDto.builder()
                    .stationName(station.getStationName())
                    .stationCode(station.getStationCode())
                    .lineNum(station.getLineNum())
                    .railOpr(station.getRailOpr())
                    .odsayStationId(station.getOdsayStationID())
                    .odsayLaneType(station.getOdsayLaneType())
                    .x(station.getX())
                    .y(station.getY())
                    .build();
        } catch (Exception e){
            throw new GeneralException(ErrorStatus.NOT_FOUND_STATION_DETAIL);
        }
    }
}
