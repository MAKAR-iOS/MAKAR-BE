package makar.dev.common.exception;

import lombok.extern.slf4j.Slf4j;
import makar.dev.common.response.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GeneralException.class })
    protected ApiResponse<String> handleException(GeneralException e) {
        return ApiResponse.FailureResponse(e.getErrorStatus());
    }

    // 모든 예외 -> 위의 예외 처리에서 못 받았을 경우
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ApiResponse<String> response =
                new ApiResponse<>(500, "FAILURE", e.getMessage(), "");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

