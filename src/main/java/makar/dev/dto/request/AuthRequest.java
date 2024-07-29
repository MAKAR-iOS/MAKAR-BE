package makar.dev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignInRequest {
        @NotBlank
        @Schema(description = "아이디")
        private String id;

        @NotBlank
        @Schema(description = "비밀번호")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequest {
        @NotBlank
        @Schema(description = "아이디")
        private String id;

        @NotBlank
        @Schema(description = "비밀번호")
        private String password;

        @NotBlank
        @Schema(description = "이메일")
        private String email;

        @NotBlank
        @Schema(description = "닉네임")
        private String username;
    }
}
