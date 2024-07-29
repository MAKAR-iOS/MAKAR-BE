package makar.dev.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import makar.dev.common.security.dto.AuthTokenDto;

public class AuthResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignDto {
        private AuthTokenDto accessToken;
        private AuthTokenDto refreshToken;
    }
}
