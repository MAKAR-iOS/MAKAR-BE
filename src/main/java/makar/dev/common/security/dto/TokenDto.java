package makar.dev.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private Long userId;

    public static TokenDto of(Long userId) {
        return new TokenDto(userId);
    }
}
