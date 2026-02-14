package com.gucardev.eventsphere.infrastructure.config.security.dto.response;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private UserResponseDto userResponseDto;
}
