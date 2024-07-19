package makar.dev.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RouteRequest {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRouteDto {
        public String sourceStationName;
        public int sourceLineNum;
        public String destinationStationName;
        public int destinationLineNum;
    }
}
