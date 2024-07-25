package makar.dev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NotiRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotiDto {
        @NotBlank
        @Schema(description = "경로 id")
        private Long routeId;

        @NotBlank
        @Schema(description = "알림 시간")
        private int notiMinute;

    }
}
