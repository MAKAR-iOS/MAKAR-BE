package makar.dev.converter;

import makar.dev.common.enums.Notification;
import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.domain.User;

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
}
