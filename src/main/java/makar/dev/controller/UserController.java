package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "데이터베이스 초기화",
            description = "Station, LineMap, Transfer 데이터를 파싱하고 데이터 베이스를 초기화합니다."
    )
    @GetMapping("/init")
    public ApiResponse initStation(){
        userService.initDatabase();
        return ApiResponse.SuccessResponse(SuccessStatus._OK);
    }

    @Operation(
            summary = "홈 화면 조회",
            description = "홈 화면을 조회합니다."
    )
    @GetMapping("/home")
    public ApiResponse getHome(@AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._HOME_GET, userService.getHome(tokenDto));
    }

}
