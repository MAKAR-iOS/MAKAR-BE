package makar.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.SuccessStatus;
import makar.dev.dto.request.StationRequest;
import makar.dev.service.StationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/station")
public class StationController {
    private final StationService stationService;

    @Operation(
            summary = "역 검색",
            description = "역 이름을 파라미터로 받아 역을 검색합니다."
    )
    @GetMapping("")
    public ApiResponse searchStation(@RequestParam(required = true, value = "q") String stationName){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_GET, stationService.searchStation(stationName));
    }

    @Operation(
            summary = "역 세부 정보 조회",
            description = "역 이름을 파라미터로 받아 역의 세부 정보를 조회합니다."
    )
    @GetMapping("/detail")
    public ApiResponse getStationDetail(@RequestParam(required = true, value = "q") String stationName, @RequestParam(required = true, value = "line") String lineNum){
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_DETAIL_GET, stationService.getStationDetail(stationName, lineNum));
    }

    @Operation(
            summary = "역 세부 정보 수정",
            description = "대중교통 정류장 조회 API를 호출해 Station 객체의 필드를 수정합니다."
    )
    @PatchMapping("/detail")
    public ApiResponse updateStationDetail(@RequestParam(required = true, value = "q") String stationName, @RequestParam(required = true, value = "line") String lineNum) {
        return ApiResponse.SuccessResponse(SuccessStatus._STATION_DETAIL_PATCH, stationService.updateStationDetail(stationName, lineNum));
    }

    @Operation(
            summary = "즐겨찾는 역(집) 업데이트",
            description = "즐겨찾는 역(집)을 업데이트합니다."
    )
    @PatchMapping("/favorite/home")
    public ApiResponse updateFavoriteHomeStation(@RequestBody StationRequest.FavoriteStationDto favoriteStationDto,
                                                 @AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_HOME_STATION_PATCH, stationService.updateFavoriteHomeStation(favoriteStationDto, tokenDto.getUserId()));
    }

    @Operation(
            summary = "즐겨찾는 역(학교) 업데이트",
            description = "즐겨찾는 역(학교)을 업데이트합니다."
    )
    @PatchMapping("/favorite/school")
    public ApiResponse updateFavoriteSchoolStation(@RequestBody StationRequest.FavoriteStationDto favoriteStationDto,
                                                   @AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_SCHOOL_STATION_PATCH, stationService.updateFavoriteSchoolStation(favoriteStationDto, tokenDto.getUserId()));
    }

    @Operation(
            summary = "즐겨찾는 역(잡) 조회",
            description = "즐겨찾는 역(집)의 정보를 조회합니다."
    )
    @GetMapping("/favorite/home")
    public ApiResponse getFavoriteHomeStation(@AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_HOME_STATION_GET, stationService.getFavoriteHomeStation(tokenDto.getUserId()));
    }

    @Operation(
            summary = "즐겨찾는 역(학교) 조회",
            description = "즐겨찾는 역(학교)의 정보를 조회합니다."
    )
    @GetMapping("/favorite/school")
    public ApiResponse getFavoriteSchoolStation(@AuthenticationPrincipal TokenDto tokenDto){
        return ApiResponse.SuccessResponse(SuccessStatus._FAVORITE_SCHOOL_STATION_GET, stationService.getFavoriteSchoolStation(tokenDto.getUserId()));

    }


}
