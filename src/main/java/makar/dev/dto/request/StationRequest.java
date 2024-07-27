package makar.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StationRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteStationDto {
        public String stationName;
        public String lineNum;
    }
}
