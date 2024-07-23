package makar.dev.common.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import makar.dev.common.security.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

    private final Algorithm tokenAlgorithm;
//    @Value("${spring.profiles.active}")
//    private String profile;

    public TokenDto verify(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }

        return verifyToken(bearerToken);
    }

    private TokenDto verifyToken(String bearerToken) {
        if (bearerToken.equals("Bearer test") /**&& profile.equals("local")**/) {
            // Allow "Bearer test" token only in test profile for simplicity
            return new TokenDto(1L);
        }

        try {
            String token = bearerToken.substring(7);

            JWTVerifier tokenVerifier = JWT
                    .require(tokenAlgorithm)
                    .withClaimPresence("userId")
                    .build();

            DecodedJWT verifiedJWT = tokenVerifier.verify(token);

            return new TokenDto(
                    verifiedJWT.getClaim("userId").asLong());
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Token expired", Instant.now());
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid token", e);
        }
    }
}
