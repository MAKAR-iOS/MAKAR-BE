package makar.dev.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import makar.dev.common.ErrorReasonDto;
import makar.dev.common.status.ErrorStatus;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public ErrorReasonDto getReason(){
        return this.errorStatus.getReason();
    }

    public ErrorReasonDto getReasonHttpStatus(){
        return this.errorStatus.getReasonHttpStatus();
    }
}
