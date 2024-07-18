package makar.dev.converter;

import makar.dev.domain.Transfer;

public class TransferConverter {
    public static Transfer toTransfer(String stationName, int fromLineNum, int fromStationId, int toLineNum, int toStationId, int transferTime){
        return Transfer.builder()
                .odsayStationName(stationName)
                .fromLineNum(fromLineNum)
                .fromStationId(fromStationId)
                .toLineNum(toLineNum)
                .toStationId(toStationId)
                .transferTime(transferTime)
                .build();
    }

}
