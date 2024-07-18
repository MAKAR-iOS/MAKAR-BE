package makar.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class StationResponse {

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationDto {
        private String stationName;
        private int lineNum;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDto {
        private List<StationDto> stationDtoList;
    }
}
