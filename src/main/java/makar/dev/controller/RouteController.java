package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.response.RouteResponse;
import makar.dev.service.RouteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/route")
public class RouteController {
    private final RouteService routeService;

    @Operation(
            summary = "경로 리스트 검색",
            description = "출발역과 도착역을 파라미터로 받아 역 간의 경로 리스트를 검색합니다."
    )
    @GetMapping()
    public ApiResponse searchRoute(@RequestParam(value = "fromStationName") String fromStationName, @RequestParam(value = "fromLineNum") String fromLineNum,
                                   @RequestParam(value = "toStationName") String toStationName, @RequestParam(value = "toLineNum") String toLineNum) {
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_LIST_GET, routeService.searchRoute(fromStationName, fromLineNum, toStationName, toLineNum));
    }

    @Operation(
            summary = "경로 설정",
            description = "경로를 설정합니다."
    )
    @PostMapping()
    public ApiResponse setRoute(@AuthenticationPrincipal TokenDto tokenDto, @RequestParam(value = "routeId") Long routeId){
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_POST, routeService.setRoute(tokenDto.getUserId(), routeId));
    }

    @Operation(
            summary = "경로 삭제",
            description = "설정된 경로를 삭제합니다."
    )
    @DeleteMapping()
    public ApiResponse deleteRoute(@AuthenticationPrincipal TokenDto tokenDto){
        routeService.deleteRoute(tokenDto.getUserId());
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_DELETE);
    }

    @Operation(
            summary = "설정된 경로 세부 정보 조회",
            description = "설정된 경로의 세부 정보를 조회합니다."
    )
    @GetMapping("/set")
    public ApiResponse getSetRoute(@AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._SET_ROUTE_GET, routeService.getSetRoute(tokenDto.getUserId()));
    }

    @Operation(
            summary = "최근 경로 리스트 조회",
            description = "최근에 설정된 경로 최대 5개를 조회합니다."
    )
    @GetMapping("/recent")
    public ApiResponse<RouteResponse.RecentRouteListDto> getAllRecentRoute(@AuthenticationPrincipal TokenDto tokenDto) {
        return ApiResponse.SuccessResponse(SuccessStatus._RECENT_ROUTE_LIST_GET, routeService.getAllRecentRoute(tokenDto.getUserId()));
    }

    @Operation(
            summary = "특정 최근 경로 삭제",
            description = "최근 경로 리스트에서 특정 경로가 삭제됩니다."
    )
    @DeleteMapping("/recent/{route-id}")
    public ApiResponse deleteRecentRoute(@AuthenticationPrincipal TokenDto tokenDto, @PathVariable("route-id") Long routeId) {
        routeService.deleteRecentRoute(tokenDto.getUserId(), routeId);
        return ApiResponse.SuccessResponse(SuccessStatus._RECENT_ROUTE_DELETE);
    }

    @Operation(
            summary = "모든 최근 경로 삭제",
            description = "최근 경로 리스트에서 모든 경로가 삭제됩니다."
    )
    @DeleteMapping("/recent/all")
    public ApiResponse deleteAllRecentRoute(@AuthenticationPrincipal TokenDto tokenDto) {
        routeService.deleteAllRecentRoute(tokenDto.getUserId());
        return ApiResponse.SuccessResponse(SuccessStatus._All_RECENT_ROUTE_DELETE);
    }
}
