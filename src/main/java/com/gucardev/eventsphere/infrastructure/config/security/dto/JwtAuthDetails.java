package com.gucardev.eventsphere.infrastructure.config.security.dto;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication details for JWT token-based authentication.
 * Does not contain password information as authentication is proven by the JWT signature.
 */
@Getter
public class JwtAuthDetails implements AuthenticationDetails {

    private final UserResponseDto userDto;

    public JwtAuthDetails(UserResponseDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserResponseDto cannot be null");
        }

        this.userDto = userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> authorities = userDto.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Set.of();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return userDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}