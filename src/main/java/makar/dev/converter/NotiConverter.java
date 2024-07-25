package makar.dev.converter;

import makar.dev.common.enums.Notification;
import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.domain.User;
import makar.dev.dto.response.NotiResponse;

import java.util.List;

public class NotiConverter {
    public static Noti toMAKARNoti(Route route, User user, int minute){
        return Noti.builder()
                .route(route)
                .user(user)
                .notiType(Notification.MAKAR)
                .noti_minute(minute)
                .build();
    }
    public static Noti toGetOffNoti(Route route, User user, int minute){
        return Noti.builder()
                .route(route)
                .user(user)
                .notiType(Notification.GETOFF)
                .noti_minute(minute)
                .build();
    }

    public static NotiResponse.NotiDto toNotiDto(Noti noti) {
        return NotiResponse.NotiDto.builder()
                .notiId(noti.getNotiId())
                .notiType(noti.getNotiType())
                .notiMinute(noti.getNoti_minute())
                .build();
    }

    public static NotiResponse.NotiListDto toNotiListDto(List<Noti> makarNotiList, List<Noti> getoffNotiList){
        List<NotiResponse.NotiDto> makarNotiDtoList = makarNotiList.stream()
                .map(NotiConverter::toNotiDto)
                .toList();
        List<NotiResponse.NotiDto> getoffNotiDtoList = getoffNotiList.stream()
                .map(NotiConverter::toNotiDto)
                .toList();
        return NotiResponse.NotiListDto.builder()
                .makarNotiDtoList(makarNotiDtoList)
                .getoffNotiDtoList(getoffNotiDtoList)
                .build();
    }
}
