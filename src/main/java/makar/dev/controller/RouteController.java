package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.RouteRequest;
import makar.dev.service.RouteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/route")
public class RouteController {
    private final RouteService routeService;

    // 경로 리스트 조회
    @GetMapping()
    public ApiResponse searchRoute(@RequestBody RouteRequest.SearchRouteDto searchRouteDto) {
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_LIST_GET, routeService.searchRoute(searchRouteDto));
    }

    // 경로 설정
    @PostMapping()
    public ApiResponse setRoute(@AuthenticationPrincipal TokenDto tokenDto, @RequestParam(value = "routeId") Long routeId){
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_POST, routeService.setRoute(tokenDto.getUserId(), routeId));
    }

    // 경로 삭제
    @DeleteMapping()
    public ApiResponse deleteRoute(@AuthenticationPrincipal TokenDto tokenDto){
        routeService.deleteRoute(tokenDto.getUserId());
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_DELETE);
    }


}
