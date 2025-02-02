package makar.dev.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.security.jwt.JwtVerifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtVerifier jwtVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        TokenDto verifiedTokenDto = jwtVerifier.verify(request);

        authenticate(verifiedTokenDto);

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken authenticate(TokenDto tokenDto) {
        UsernamePasswordAuthenticationToken authenticationToken = null;
        if (tokenDto != null) {
            authenticationToken = new UsernamePasswordAuthenticationToken(
                    tokenDto,
                    tokenDto.getUserId(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return authenticationToken;
    }
}
