package makar.dev.common.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import makar.dev.common.security.dto.AuthTokenDto;
import makar.dev.common.security.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtService {
    private final Algorithm tokenAlgorithm;
    @Value("${app.jwt.accessTokenValidMS}") private Long accessTokenValidMilliseconds;
    @Value("${app.jwt.refreshTokenValidMS}") private Long refreshTokenValidMilliseconds;


    private AuthTokenDto createToken(Long id, Long tokenValidMilliseconds) {
        Date expiredAt = new Date(System.currentTimeMillis() + tokenValidMilliseconds);

        String token = JWT.create()
                .withClaim("userId", id)
                .withExpiresAt(expiredAt)
                .sign(tokenAlgorithm);

        return new AuthTokenDto(token, tokenValidMilliseconds);
    }

    public TokenDto getTokenDto() {
        TokenDto tokenDto = (TokenDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (tokenDto == null) {
            throw new RuntimeException("TokenDto is null");
        }
        return tokenDto;
    }

    public AuthTokenDto createAccessToken(Long id) {
        return createToken(id, accessTokenValidMilliseconds);
    }

    public AuthTokenDto createRefreshToken(Long id) {
        return createToken(id, refreshTokenValidMilliseconds);
    }
}
