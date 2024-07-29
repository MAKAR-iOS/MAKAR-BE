package makar.dev.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {
    _OK(HttpStatus.OK, 200, "OK"),

    // StationController
    _STATION_GET(HttpStatus.OK,200, "역 검색이 완료되었습니다"),
    _STATION_DETAIL_GET(HttpStatus.OK, 200, "역 세부 정보 조회가 완료되었습니다."),
    _STATION_DETAIL_PATCH(HttpStatus.OK, 200, "역 세부 정보 수정이 완료되었습니다."),
    _FAVORITE_HOME_STATION_GET(HttpStatus.OK, 200, "즐겨찾는 역(집) 조회가 완료되었습니다."),
    _FAVORITE_SCHOOL_STATION_GET(HttpStatus.OK, 200, "즐겨찾는 역(학교) 조회가 완료되었습니다."),
    _FAVORITE_HOME_STATION_PATCH(HttpStatus.OK, 200, "즐겨찾는 역(집) 업데이트가 완료되었습니다."),
    _FAVORITE_SCHOOL_STATION_PATCH(HttpStatus.OK, 200, "즐겨찾는 역(학교) 업데이트가 완료되었습니다."),

    // RouteController
    _ROUTE_LIST_GET(HttpStatus.OK, 200, "경로 리스트 검색이 완료되었습니다."),
    _ROUTE_DELETE(HttpStatus.OK, 200, "경로 삭제가 완료되었습니다."),
    _ROUTE_POST(HttpStatus.OK, 200, "경로 설정이 완료되었습니다."),
    _SET_ROUTE_GET(HttpStatus.OK, 200, "설정된 경로 조회가 완료되었습니다."),
    _RECENT_ROUTE_LIST_GET(HttpStatus.OK, 200, "최근 경로 리스트 조회가 완료되었습니다."),

    // NotiController
    _MAKAR_NOTI_POST(HttpStatus.OK, 200, "막차 알림 추가가 완료되었습니다."),
    _MAKAR_NOTI_DELETE(HttpStatus.OK, 200, "막차 알림 삭제가 완료되었습니다."),
    _GETOFF_NOTI_POST(HttpStatus.OK, 200, "하차 알림 추가가 완료되었습니다."),
    _GETOFF_NOTI_DELETE(HttpStatus.OK, 200, "하차 알림 삭제가 완료되었습니다."),
    _NOTI_LIST_GET(HttpStatus.OK, 200, "알림 리스트 조회가 완료되었습니다."),

    // UserController
    _HOME_GET(HttpStatus.OK, 200, "홈 화면 조회가 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
