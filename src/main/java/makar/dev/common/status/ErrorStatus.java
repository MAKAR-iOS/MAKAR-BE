package makar.dev.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import makar.dev.common.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    /**
     *  Error Code
     *  400 : 잘못된 요청
     *  401 : JWT에 대한 오류
     *  403 : 요청한 정보에 대한 권한 없음.
     *  404 : 존재하지 않는 정보에 대한 요청.
     */

    /**
     * Code : 400
     * Bad Request
     */
    INVALID_API_KEY(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 API Key입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400,  "유효하지 않은 요청입니다."),
    INVALID_SOURCE_TIME_FORMAT(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 막차 시간 형식으로 인해 도착 시간 계산 중 오류가 발생했습니다."),
    ALREADY_ROUTE_SET(HttpStatus.BAD_REQUEST, 400, "이미 설정된 경로가 존재합니다."),
    INVALID_DELETE_ROUTE(HttpStatus.BAD_REQUEST, 400, "설정된 경로가 존재하지 않습니다."),
    INVALID_SET_ROUTE(HttpStatus.BAD_REQUEST, 400, "설정된 경로가 존재하지 않습니다."),
    INVALID_NOTI_MINUTE(HttpStatus.BAD_REQUEST, 400, "이미 설정된 알림입니다."),

    /**
     * Code : 403
     */
    FORBIDDEN_ROUTE(HttpStatus.FORBIDDEN, 403, "해당 경로에 대한 권한이 없습니다."),

    /**
     * Code : 404
     */
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 404, "존재하지 않는 유저입니다."),
    NOT_FOUND_STATION(HttpStatus.NOT_FOUND, 404, "존재하지 않는 역 정보입니다."),
    NOT_FOUND_STATION_DETAIL(HttpStatus.NOT_FOUND, 404, "해당 역의 세부 정보가 존재하지 않습니다."),
    NOT_FOUND_FAVORITE_STATION(HttpStatus.NOT_FOUND, 404, "즐겨찾는 역이 존재하지 않습니다."),
    NOT_FOUND_LINE_MAP(HttpStatus.NOT_FOUND, 404, "지하철 노선도 정보가 존재하지 않습니다."),
    NOT_FOUND_LINE_STATION(HttpStatus.NOT_FOUND, 404, "지하철 노선도 역 리스트가 존재하지 않습니다."),
    NOT_FOUND_ROUTE(HttpStatus.NOT_FOUND, 404, "존재하지 않는 경로입니다."),


    /**
     * Code : 500
     */
    FAILURE_STATION_DATA_INIT(HttpStatus.NOT_IMPLEMENTED, 500, "역 데이터 초기화에 실패했습니다."),
    FAILURE_LINEMAP_DATA_INIT(HttpStatus.NOT_IMPLEMENTED, 500, "노선도 데이터 초기화에 실패했습니다."),
    FAILURE_TRANSFER_DATA_INIT(HttpStatus.NOT_IMPLEMENTED, 500, "환승 데이터 초기화에 실패했습니다."),
    FAILURE_API_REQUEST(HttpStatus.NOT_IMPLEMENTED, 500, "API 호출 중 오류가 발생했습니다."),
    FAILURE_MAKAR_TIME(HttpStatus.NOT_IMPLEMENTED, 500, "막차 시간 계산 중 오류가 발생했습니다."),
    FAILURE_ASYNC_TASK(HttpStatus.NOT_IMPLEMENTED, 500, "비동기 작업 중 오류가 발생했습니다."),
    FAILURE_READ_EXCEL_FILE(HttpStatus.NOT_IMPLEMENTED, 500, "엑셀 파일을 읽는 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    public ErrorReasonDto getReason(){
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .success(false)
                .build();
    }

    public ErrorReasonDto getReasonHttpStatus(){
        return ErrorReasonDto.builder()
                .httpStatus(httpStatus)
                .message(message)
                .code(code)
                .success(false)
                .build();
    }
}
