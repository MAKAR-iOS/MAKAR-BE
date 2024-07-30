package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.AuthRequest;
import makar.dev.dto.response.AuthResponse;
import makar.dev.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "회원가입 후 토큰을 반환합니다."
    )
    @PostMapping("/sign-up")
    public ApiResponse<AuthResponse.SignDto> signUp(@RequestBody AuthRequest.SignUpRequest request) {
        AuthResponse.SignDto response = authService.signUp(request);

        return ApiResponse.SuccessResponse(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "로그인",
            description = "로그인 후 토큰을 반환합니다."
    )
    @PostMapping("/sign-in")
    public ApiResponse<AuthResponse.SignDto> signIn(@RequestBody AuthRequest.SignInRequest request) {
        AuthResponse.SignDto response = authService.signIn(request.getId(), request.getPassword());

        return ApiResponse.SuccessResponse(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "로그아웃",
            description = "로그아웃 후 DB에 저장하고 있는 리프레쉬 토큰을 삭제합니다."
    )
    @PostMapping("/sign-out")
    public ApiResponse<String> signOut(@AuthenticationPrincipal TokenDto tokenDto) {
        authService.signOut(tokenDto.getUserId());
        return ApiResponse.SuccessResponse(SuccessStatus._OK);
    }
}
