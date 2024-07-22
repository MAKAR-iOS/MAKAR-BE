package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.status.SuccessStatus;
import makar.dev.service.RouteService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/route")
public class RouteController {
    private final RouteService routeService;

    // 경로 리스트 조회
    @GetMapping()
    public ApiResponse searchRoute(@RequestParam(value = "fromStationName") String fromStationName, @RequestParam(value = "fromLineNum") int fromLineNum,
                                   @RequestParam(value = "toStationName") String toStationName, @RequestParam(value = "toLineNum") int toLineNum) {
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_LIST_GET, routeService.searchRoute(fromStationName, fromLineNum, toStationName, toLineNum));
    }

    // 경로 설정
    @PostMapping()
    public ApiResponse setRoute(@RequestParam(value = "userId") Long userId, @RequestParam(value = "routeId") Long routeId){
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_POST, routeService.setRoute(userId, routeId));
    }

    // 경로 삭제
    @DeleteMapping()
    public ApiResponse deleteRoute(@RequestParam(value = "userId") Long userId){
        routeService.deleteRoute(userId);
        return ApiResponse.SuccessResponse(SuccessStatus._ROUTE_DELETE);
    }


}
