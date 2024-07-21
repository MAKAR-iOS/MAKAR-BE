package makar.dev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @NotBlank
    @Schema(description = "아이디")
    private String id;

    @NotBlank
    @Schema(description = "비밀번호")
    private String password;
}
