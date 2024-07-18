package makar.dev.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {
    _OK(HttpStatus.OK, 200, "OK"),

    _STATION_GET(HttpStatus.OK,200, "역 검색이 완료되었습니다");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
