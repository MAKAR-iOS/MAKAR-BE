package makar.dev.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {
    _OK(HttpStatus.OK, 200, "OK"),

    _STATION_GET(HttpStatus.OK,200, "역 검색이 완료되었습니다"),
    _ROUTE_LIST_GET(HttpStatus.OK, 200, "경로 리스트 검색이 완료되었습니다."),
    _FAVORITE_HOME_STATION_PATCH(HttpStatus.OK, 200, "즐겨찾는 역(집) 업데이트가 완료되었습니다."),
    _FAVORITE_SCHOOL_STATION_PATCH(HttpStatus.OK, 200, "즐겨찾는 역(학교) 업데이트가 완료되었습니다.");



    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
