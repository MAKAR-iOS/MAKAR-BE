package makar.dev.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.StationConverter;
import makar.dev.domain.Station;
import makar.dev.domain.User;
import makar.dev.dto.request.StationRequest;
import makar.dev.dto.response.StationResponse;
import makar.dev.repository.StationRepository;
import makar.dev.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;

    // 역 검색
    public StationResponse.SearchDto searchStation(String param){
        List<Station> stationList = stationRepository.findByStationNameContaining(param);
        return StationConverter.toSearchDto(stationList);
    }

    // 즐겨찾는 역(집) 업데이트
    @Transactional
    public StationResponse.StationDto updateFavoriteHomeStation(StationRequest.FavoriteStationDto favoriteStationDto){
        User user = findById(favoriteStationDto.getUserId());
        Station station = findByStationNameAndOdsayLaneType(favoriteStationDto.getStationName(), favoriteStationDto.getLineNum());
        user.updateFavoriteHomeStation(station);
        return StationConverter.toStationDto(station);
    }

    // 즐겨찾는 역(학교) 업데이트
    @Transactional
    public StationResponse.StationDto updateFavoriteSchoolStation(StationRequest.FavoriteStationDto favoriteStationDto){
        User user = findById(favoriteStationDto.getUserId());
        Station station = findByStationNameAndOdsayLaneType(favoriteStationDto.getStationName(), favoriteStationDto.getLineNum());
        user.updateFavoriteSchoolStation(station);
        return StationConverter.toStationDto(station);
    }

    // 즐겨찾는 역(집) 조회
    public StationResponse.StationDto getFavoriteHomeStation(Long userId){
        User user = findById(userId);

        if (!user.isFavoriteHomeStationExist())
            throw new GeneralException(ErrorStatus.NOT_FOUND_FAVORITE_STATION);

        Station station = user.getFavoriteHomeStation();
        return StationConverter.toStationDto(station);
    }

    // 즐겨찾는 역(학교) 조회
    public StationResponse.StationDto getFavoriteSchoolStation(Long userId){
        User user = findById(userId);

        if (!user.isFavoriteSchoolStationExist())
            throw new GeneralException(ErrorStatus.NOT_FOUND_FAVORITE_STATION);

        Station station = user.getFavoriteSchoolStation();
        return StationConverter.toStationDto(station);
    }

    private User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));
    }

    private Station findByStationNameAndOdsayLaneType(String stationName, int odsayLaneType){
     return stationRepository.findByStationNameAndOdsayLaneType(stationName, odsayLaneType)
             .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }


}
