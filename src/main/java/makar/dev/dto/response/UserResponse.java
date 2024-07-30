package makar.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeDto {
        private boolean isRouteSet;
        private String sourceStationName;
        private String destinationStationName;
        private String makarTime;
        private String getOffTime;
        private List<NotiResponse.NotiDto> makarNotiList;
        private List<NotiResponse.NotiDto> getOffNotiList;
    }
}
