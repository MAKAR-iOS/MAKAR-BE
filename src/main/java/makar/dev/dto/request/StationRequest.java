package makar.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StationRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteStationDto {
        public Long userId;
        public String stationName;
        public int lineNum;
    }
}
