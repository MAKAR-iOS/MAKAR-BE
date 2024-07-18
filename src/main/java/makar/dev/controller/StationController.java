package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.status.SuccessStatus;
import makar.dev.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/station")
public class StationController {
    private final StationService stationService;

    @GetMapping("")
    public ApiResponse searchStation(@RequestParam(required = true, value = "q") String stationName){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_GET, stationService.searchStation(stationName));
    }


}
