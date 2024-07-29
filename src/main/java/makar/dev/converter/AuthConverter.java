package makar.dev.converter;

import makar.dev.common.security.dto.AuthTokenDto;
import makar.dev.dto.response.AuthResponse;

public class AuthConverter {
    public static AuthResponse.SignDto toSignDto(AuthTokenDto accessToken, AuthTokenDto refreshToken){
        return AuthResponse.SignDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
