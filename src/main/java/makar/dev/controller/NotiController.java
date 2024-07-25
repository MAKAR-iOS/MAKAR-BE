package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.enums.Notification;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.NotiRequest;
import makar.dev.service.NotiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponse.SuccessResponse(SuccessStatus._MAKAR_NOTI_POST, notiService.postNoti(notiDto, tokenDto, Notification.MAKAR));
    }

    @Operation(
            summary = "막차 알림 삭제",
            description = "설정된 경로의 막차 알림을 삭제하고 유저의 알림 리스트를 반환합니다."
    )
    @DeleteMapping("/makar")
    public ApiResponse deleteMakarNoti(@RequestParam(value = "notiId") Long notiId,
                                     @AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._MAKAR_NOTI_DELETE, notiService.deleteNoti(notiId, tokenDto, Notification.MAKAR));
    }

    @Operation(
            summary = "하차 알림 추가",
            description = "설정된 경로에 하차 알림을 추가합니다."
    )
    @PostMapping("/getoff")
    public ApiResponse postGetOffNoti(@RequestBody NotiRequest.NotiDto notiDto,
                                     @AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._GETOFF_NOTI_POST, notiService.postNoti(notiDto, tokenDto, Notification.GETOFF));
    }

    @Operation(
            summary = "하차 알림 삭제",
            description = "설정된 경로의 하차 알림을 삭제하고 유저의 알림 리스트를 반환합니다."
    )
    @DeleteMapping("/getoff")
    public ApiResponse deleteGetOffNoti(@RequestParam(value = "notiId") Long notiId,
                                       @AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._GETOFF_NOTI_DELETE, notiService.deleteNoti(notiId, tokenDto, Notification.GETOFF));
    }

    @Operation(
            summary = "알림 리스트 조회",
            description = "설정된 경로에 대한 유저의 알림 리스트를 조회합니다."
    )
    @GetMapping("")
    public ApiResponse getNotiList(@AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._NOTI_LIST_GET, notiService.getNotiList(tokenDto));
    }

}
