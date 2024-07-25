package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.NotiRequest;
import makar.dev.service.NotiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
public class NotiController {
    private final NotiService notiService;

    @Operation(
            summary = "막차 알림 추가",
            description = "설정된 경로에 막차 알림을 추가합니다."
    )
    @PostMapping("/makar")
    public ApiResponse postMakarNoti(@RequestBody NotiRequest.NotiDto notiDto,
                                     @AuthenticationPrincipal TokenDto tokenDto){
        notiService.postMakarNoti(notiDto, tokenDto);
        return ApiResponse.SuccessResponse(SuccessStatus._MAKAR_NOTI_POST);
    }


}
