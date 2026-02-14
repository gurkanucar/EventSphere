package com.gucardev.eventsphere.infrastructure.config.security.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Base interface for authentication details.
 * Provides common contract for both password-based and JWT-based authentication.
 */
public interface AuthenticationDetails {

    String getUsername();

    Collection<? extends GrantedAuthority> getAuthorities();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}