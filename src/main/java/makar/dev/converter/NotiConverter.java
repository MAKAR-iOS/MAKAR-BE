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

    public static NotiResponse.NotiListDto toNotiListDto(List<Noti> notiList){
        List<NotiResponse.NotiDto> notiDtoList = notiList.stream()
                .map(NotiConverter::toNotiDto)
                .toList();
        return NotiResponse.NotiListDto.builder()
                .notiDtoList(notiDtoList)
                .build();
    }
}
