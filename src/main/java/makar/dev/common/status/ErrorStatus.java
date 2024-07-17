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


    /**
     * Code : 500
     */
    FAILURE_DATA_INIT(HttpStatus.NOT_IMPLEMENTED, 500, "데이터 초기화에 실패했습니다.");

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
