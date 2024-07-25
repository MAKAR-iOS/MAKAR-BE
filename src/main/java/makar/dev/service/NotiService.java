package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.common.enums.Notification;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.NotiConverter;
import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.domain.User;
import makar.dev.dto.request.NotiRequest;
import makar.dev.repository.NotiRepository;
import makar.dev.repository.RouteRepository;
import makar.dev.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotiService {
    private final UserRepository userRepository;
    private final NotiRepository notiRepository;
    private final RouteRepository routeRepository;

    // 막차 알림 추가
    public void postMakarNoti(NotiRequest.NotiDto notiDto, TokenDto tokenDto){
        User user = findUserById(tokenDto.getUserId());
        Route route = findRouteById(notiDto.getRouteId());

        // 경로 설정이 안되어 있을 경우
        if (user.getNotiList().isEmpty())
            throw new GeneralException(ErrorStatus.INVALID_SET_ROUTE);

        // 알림 시간 중복 확인
        int notiMinute = checkNotiMinute(notiDto.getNotiMinute(), user, route, Notification.MAKAR);

        // noti 생성
        Noti noti = NotiConverter.toMAKARNoti(route, user, notiMinute);
        notiRepository.save(noti);
        user.addNotiList(noti);
    }

    private static int checkNotiMinute(int notiMinute, User user, Route route, Notification notiType) {
        List<Noti> notiList = user.getNotiList();

        // 경로에 대한 권한이 없을 경우
        if (notiList.get(0).getRoute() != route)
            throw new GeneralException(ErrorStatus.FORBIDDEN_ROUTE);

        for (Noti noti : notiList){
            if (noti.getNotiType() != notiType)
                continue;

            // 이미 해당 시간에 대한 알림이 존재할 경우
            if (noti.getNoti_minute() == notiMinute)
                throw new GeneralException(ErrorStatus.INVALID_NOTI_MINUTE);
        }
        return notiMinute;
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));
    }

    private Route findRouteById(Long routeId){
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_ROUTE));
    }
}
