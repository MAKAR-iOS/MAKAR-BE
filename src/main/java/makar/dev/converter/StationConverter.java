package makar.dev.converter;

import makar.dev.domain.Station;

public class StationConverter {
    public static Station toStation(String stationName, String stationCode, String lineNum, String railOpr){
        return Station.builder()
                .stationName(stationName)
                .stationCode(stationCode)
                .lineNum(lineNum)
                .railOpr(railOpr)
                .build();
    }
}
