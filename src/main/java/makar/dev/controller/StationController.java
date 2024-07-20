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

    @GetMapping("")
    public ApiResponse searchStation(@RequestParam(required = true, value = "q") String stationName){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_GET, stationService.searchStation(stationName));
    }

    @PatchMapping("/favorite/home")
    public ApiResponse updateFavoriteHomeStation(@RequestBody StationRequest.FavoriteStationDto favoriteStationDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_HOME_STATION_PATCH, stationService.updateFavoriteHomeStation(favoriteStationDto));
    }

    @PatchMapping("/favorite/school")
    public ApiResponse updateFavoriteSchoolStation(@RequestBody StationRequest.FavoriteStationDto favoriteStationDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_SCHOOL_STATION_PATCH, stationService.updateFavoriteSchoolStation(favoriteStationDto));
    }

    @GetMapping("/favorite/home/{userId}")
    public ApiResponse getFavoriteHomeStation(@PathVariable(name = "userId") Long userId){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_HOME_STATION_GET, stationService.getFavoriteHomeStation(userId));
    }

    @GetMapping("/favorite/school/{userId}")
    public ApiResponse getFavoriteSchoolStation(@PathVariable(name = "userId") Long userId){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_SCHOOL_STATION_GET, stationService.getFavoriteSchoolStation(userId));
    }


}
