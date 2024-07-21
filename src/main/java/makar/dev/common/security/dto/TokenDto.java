package makar.dev.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private Integer userId;

    public static TokenDto of(Integer userId) {
        return new TokenDto(userId);
    }
}
