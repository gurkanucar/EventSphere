package com.gucardev.eventsphere.infrastructure.config.security.dto;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication details for traditional username/password login.
 * Used by Spring Security's authentication manager during login.
 */
@Getter
public class PasswordAuthDetails implements UserDetails, AuthenticationDetails {

    private final UserResponseDto userDto;
    private final String password;

    public PasswordAuthDetails(UserResponseDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserResponseDto cannot be null");
        }
        if (userDto.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null for password-based authentication");
        }

        this.userDto = userDto;
        this.password = userDto.getPassword();
    }

    @Override
    public @NotNull Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> authorities = userDto.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Set.of();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public @NotNull String getUsername() {
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