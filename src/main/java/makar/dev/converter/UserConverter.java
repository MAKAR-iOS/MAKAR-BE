package makar.dev.converter;

import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.domain.User;
import makar.dev.dto.response.NotiResponse;
import makar.dev.dto.response.UserResponse;

import java.util.List;

public class UserConverter {

    public static UserResponse.HomeDto toRouteSetHomeDto(NotiResponse.NotiListDto notiListDto, Route route){
        return UserResponse.HomeDto.builder()
                .isRouteSet(true)
                .sourceStationName(route.getSourceStation().getStationName())
                .destinationStationName(route.getDestinationStation().getStationName())
                .makarTime(route.getSchedule().getSourceTime())
                .getOffTime(route.getSchedule().getDestinationTime())
                .makarNotiList(notiListDto.getMakarNotiDtoList())
                .getOffNotiList(notiListDto.getGetoffNotiDtoList())
                .build();
    }

    public static UserResponse.HomeDto toRouteUnSetHomeDto(){
        return UserResponse.HomeDto.builder()
                .isRouteSet(false)
                .sourceStationName(null)
                .destinationStationName(null)
                .makarTime(null)
                .getOffTime(null)
                .makarNotiList(null)
                .getOffNotiList(null)
                .build();
    }

    public static User toUser(String id, String password, String email, String username){
        return User.builder()
                .id(id)
                .password(password)
                .email(email)
                .username(username)
                .build();
    }
}
