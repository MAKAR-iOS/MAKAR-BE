package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.StationRequest;
import makar.dev.service.StationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/station")
public class StationController {
    private final StationService stationService;

    // 역 검색
    @GetMapping("")
    public ApiResponse searchStation(@RequestParam(required = true, value = "q") String stationName){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_GET, stationService.searchStation(stationName));
    }

    // 역 세부 정보 조회
    @GetMapping("/detail")
    public ApiResponse getStationDetail(@RequestParam(required = true, value = "q") String stationName, @RequestParam(required = true, value = "line") String lineNum){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_DETAIL_GET, stationService.getStationDetail(stationName, lineNum));
    }

    // 역 세부 정보 수정
    @PatchMapping("/detail")
    public ApiResponse updateStationDetail(@RequestParam(required = true, value = "q") String stationName, @RequestParam(required = true, value = "line") String lineNum){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_DETAIL_PATCH, stationService.updateStationDetail(stationName, lineNum));
    }


}
