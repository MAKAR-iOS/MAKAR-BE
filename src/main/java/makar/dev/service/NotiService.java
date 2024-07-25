package makar.dev.service;

import jakarta.transaction.Transactional;
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
import makar.dev.dto.response.NotiResponse;
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

    // 알림 추가
    @Transactional
    public NotiResponse.NotiDto postNoti(NotiRequest.NotiDto notiDto, TokenDto tokenDto, Notification notiType){
        User user = findUserById(tokenDto.getUserId());
        Route route = findRouteById(notiDto.getRouteId());

        // 경로 설정 확인
        validateUserRouteSet(user.getNotiList());

        // 알림 시간 중복 확인
        int notiMinute = validateNotiMinute(notiDto.getNotiMinute(), user, route, notiType);

        // noti 생성
        Noti noti = makeNotiEntity(route, user, notiMinute, notiType);
        notiRepository.save(noti);
        user.addNotiList(noti);

        return NotiConverter.toNotiDto(noti);
    }

    // 알림 삭제
    @Transactional
    public NotiResponse.NotiListDto deleteNoti(Long notiId, TokenDto tokenDto, Notification notiType){
        User user = findUserById(tokenDto.getUserId());
        List<Noti> notiList = user.getNotiList();

        // 경로 설정 확인
        validateUserRouteSet(notiList);

        Noti noti = findNotiById(notiId);
        // 알림에 대한 권한 확인
        validateNotiOwnerShip(noti, user);
        // 알림 타입 확인
        validateNotiType(noti, notiType);

        // del noti
        notiList.remove(noti);
        notiRepository.delete(noti);

        return NotiConverter.toNotiListDto(user.getNotiList());
    }

    private int validateNotiMinute(int notiMinute, User user, Route route, Notification notiType) {
        List<Noti> notiList = user.getNotiList();

        // 경로에 대한 권한 확인
        validateRouteOwnerShip(route, notiList);

        for (Noti noti : notiList){
            if (noti.getNotiType() != notiType)
                continue;

            // 이미 해당 시간에 대한 알림이 존재할 경우
            if (noti.getNoti_minute() == notiMinute)
                throw new GeneralException(ErrorStatus.INVALID_NOTI_MINUTE);
        }
        return notiMinute;
    }

    private void validateUserRouteSet(List<Noti> notiList){
        // 경로 설정이 안되어 있을 경우
        if (notiList.isEmpty())
            throw new GeneralException(ErrorStatus.INVALID_ROUTE_SET);
    }

    private void validateNotiType(Noti noti, Notification notiType){
        // 알림 타입이 유효하지 않을 경우
        if (noti.getNotiType() != notiType)
            throw new GeneralException(ErrorStatus.INVALID_NOTI_DELETE);
    }

    private void validateNotiOwnerShip(Noti noti, User user){
        // 알림에 대한 권한이 없을 경우
        if (noti.getUser() != user)
            throw new GeneralException(ErrorStatus.FORBIDDEN_NOTI);
    }

    private void validateRouteOwnerShip(Route route, List<Noti> notiList){
        // 경로에 대한 권한이 없을 경우
        if (notiList.get(0).getRoute() != route)
            throw new GeneralException(ErrorStatus.FORBIDDEN_ROUTE);
    }

    private Noti makeNotiEntity(Route route, User user, int notiMinute, Notification notiType){
        Noti noti;
        if (notiType == Notification.MAKAR)
            noti = NotiConverter.toMAKARNoti(route, user, notiMinute);
        else
            noti = NotiConverter.toGetOffNoti(route, user, notiMinute);
        return noti;
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));
    }

    private Route findRouteById(Long routeId){
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_ROUTE));
    }

    private Noti findNotiById(Long notiId){
        return notiRepository.findById(notiId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_NOTI));
    }
}
