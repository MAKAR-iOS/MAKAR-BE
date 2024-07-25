package makar.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import makar.dev.common.enums.Notification;

import java.util.List;

public class NotiResponse {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotiDto {
        private Long notiId;
        private Notification notiType;
        private int notiMinute;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotiListDto {
        private List<NotiDto> notiDtoList;
    }
}
