package com.gucardev.eventsphere.infrastructure.config.security.config;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String jwtToken;
    private final UserResponseDto user;

    public CustomUsernamePasswordAuthenticationToken(
            Object principal, Collection<? extends GrantedAuthority> authorities, String jwtToken,
            UserResponseDto user) {
        super(principal, null, authorities);
        this.jwtToken = jwtToken;
        this.user = user;
    }
}
