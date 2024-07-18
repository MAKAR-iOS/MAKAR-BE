package makar.dev.converter;

import makar.dev.domain.LineMap;
import makar.dev.domain.LineStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineConverter {
    public static LineMap toLineMap(int lineNum, String station, List<LineStation> upLine){
        List<LineStation> downLine = new ArrayList<>(upLine);
        Collections.reverse(downLine);

        return LineMap.builder()
                .lineNum(lineNum)
                .startStationName(station)
                .upLineList(upLine)
                .downLineList(downLine)
                .build();
    }

    public static LineStation toLineStation(int odsayStationId, String stationName){
        return LineStation.builder()
                .odsayStationId(odsayStationId)
                .stationName(stationName)
                .build();
    }
}
