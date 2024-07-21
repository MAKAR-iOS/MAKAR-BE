package makar.dev.common.exception;

import lombok.extern.slf4j.Slf4j;
import makar.dev.common.response.ApiResponse;
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
}

