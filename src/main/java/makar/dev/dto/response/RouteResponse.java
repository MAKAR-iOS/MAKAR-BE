package makar.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RouteResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteDto {
        private String sourceStationName;
        private int sourceLineNum;
        private String destinationStationName;
        private int destinationLineNum;
        private String sourceTime; // 출발 시각
        private String destinationTime;  // 도착 시각
        private int totalTime; // 총 소요 시간
        private List<SubRouteDto> subRouteDtoList; // 환승 정보
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubRouteDto {
        private String startStationName;
        private String endStationName;
        private int startStationCode;
        private int endStationCode;
        private int lineNum;
        private int wayCode;
        private int sectionTime;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRouteDto {
        private List<RouteDto> routeDtoList;
    }
}
