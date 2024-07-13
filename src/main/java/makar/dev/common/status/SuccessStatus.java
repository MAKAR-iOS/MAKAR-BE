package makar.dev.common.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {
    _OK(HttpStatus.OK, 200, "OK");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
