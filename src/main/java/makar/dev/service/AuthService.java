package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.common.security.dto.AuthTokenDto;
import makar.dev.common.security.jwt.JwtService;
import makar.dev.common.util.PasswordEncoder;
import makar.dev.converter.AuthConverter;
import makar.dev.dto.request.AuthRequest;
import makar.dev.dto.response.AuthResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import makar.dev.domain.User;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse.SignDto signUp(AuthRequest.SignUpRequest request) {
        Optional<User> existingUser = userService.getOptionalUserById(request.getId());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 아이디입니다.");
        }

        String password = passwordEncoder.encode(request.getPassword());
        User user = userService.createUser(request.getId(), password, request.getEmail(), request.getUsername());

        AuthTokenDto accessToken = jwtService.createAccessToken(user.getUserId());
        AuthTokenDto refreshToken = jwtService.createRefreshToken(user.getUserId());

        user.setRefreshToken(refreshToken.getToken());

        return AuthConverter.toSignDto(accessToken, refreshToken);
    }

    public AuthResponse.SignDto signIn(String id, String password) {
        User user = userService.getOptionalUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        AuthTokenDto accessToken = jwtService.createAccessToken(user.getUserId());
        AuthTokenDto refreshToken = jwtService.createRefreshToken(user.getUserId());

        user.setRefreshToken(refreshToken.getToken());

        return AuthConverter.toSignDto(accessToken, refreshToken);
    }
}
