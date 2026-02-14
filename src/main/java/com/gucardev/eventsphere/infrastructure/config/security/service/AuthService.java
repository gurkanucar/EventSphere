package com.gucardev.eventsphere.infrastructure.config.security.service;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.AuthenticationDetails;
import com.gucardev.eventsphere.infrastructure.config.security.dto.JwtAuthDetails;
import com.gucardev.eventsphere.infrastructure.config.security.dto.PasswordAuthDetails;
import com.gucardev.eventsphere.infrastructure.config.security.dto.request.LoginRequest;
import com.gucardev.eventsphere.infrastructure.config.security.dto.response.TokenDto;
import com.gucardev.eventsphere.infrastructure.util.EncryptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final EncryptionService encryptionService;

    public TokenDto login(@Valid LoginRequest loginRequest) {
        // Authenticate with username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // After successful authentication, principal will be PasswordAuthDetails
        PasswordAuthDetails authDetails = (PasswordAuthDetails) authentication.getPrincipal();

        // Generate JWT token
        String jwt = tokenService.generateToken(Objects.requireNonNull(authDetails));
        var encryptedToken = encryptionService.encryptToken(jwt);

        return new TokenDto(encryptedToken, authDetails.getUserDto());
    }

    public UserResponseDto getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            throw new RuntimeException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        // Handle both password-based and JWT-based authentication
        if (principal instanceof PasswordAuthDetails) {
            return ((PasswordAuthDetails) principal).getUserDto();
        } else if (principal instanceof JwtAuthDetails) {
            return ((JwtAuthDetails) principal).getUserDto();
        } else if (principal instanceof AuthenticationDetails) {
            // Generic fallback for any future authentication types
            log.warn("Unknown AuthenticationDetails type: {}", principal.getClass().getName());
            throw new RuntimeException("Unknown authentication type");
        }

        log.warn("Authenticated principal is not an instance of AuthenticationDetails: {}",
                Objects.requireNonNull(principal).getClass().getName());
        throw new RuntimeException("Authenticated user is not of expected type");
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String &&
                        authentication.getPrincipal().equals("anonymousUser"));
    }
}