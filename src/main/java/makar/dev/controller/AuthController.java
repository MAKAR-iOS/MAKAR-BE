package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.SignInRequest;
import makar.dev.dto.request.SignUpRequest;
import makar.dev.dto.response.SignResponse;
import makar.dev.service.AuthService;
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
    public ApiResponse<SignResponse> signUp(@RequestBody SignUpRequest request) {
        SignResponse response = authService.signUp(request);

        return ApiResponse.SuccessResponse(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "로그인",
            description = "로그인 후 토큰을 반환합니다."
    )
    @PostMapping("/sign-in")
    public ApiResponse<SignResponse> signIn(@RequestBody SignInRequest request) {
        SignResponse response = authService.signIn(request.getId(), request.getPassword());

        return ApiResponse.SuccessResponse(SuccessStatus._OK, response);
    }
}
