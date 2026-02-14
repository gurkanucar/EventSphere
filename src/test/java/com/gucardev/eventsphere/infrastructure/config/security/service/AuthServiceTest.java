package com.gucardev.eventsphere.infrastructure.config.security.service;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.JwtAuthDetails;
import com.gucardev.eventsphere.infrastructure.config.security.dto.PasswordAuthDetails;
import com.gucardev.eventsphere.infrastructure.config.security.dto.request.LoginRequest;
import com.gucardev.eventsphere.infrastructure.config.security.dto.response.TokenDto;
import com.gucardev.eventsphere.infrastructure.util.EncryptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService tokenService;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_ShouldReturnTokenDto_WhenCredentialsAreValid() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("test@example.com");
        userDto.setPassword("encodedPassword");
        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);

        String rawToken = "raw.jwt.token";
        String encryptedToken = "encrypted.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authDetails);
        when(tokenService.generateToken(authDetails)).thenReturn(rawToken);
        when(encryptionService.encryptToken(rawToken)).thenReturn(encryptedToken);

        // When
        TokenDto result = authService.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(encryptedToken);
        assertThat(result.getUserResponseDto()).isEqualTo(userDto);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(authentication);
        verify(tokenService).generateToken(authDetails);
        verify(encryptionService).encryptToken(rawToken);
    }

    @Test
    void login_ShouldThrowException_WhenAuthenticationFails() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verify(securityContext, never()).setAuthentication(any());
        verifyNoInteractions(tokenService, encryptionService);
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUserDto_WhenAuthenticatedWithPassword() {
        // Given
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setPassword("encodedPassword");
        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(authDetails);

        // When
        UserResponseDto result = authService.getAuthenticatedUser();

        // Then
        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenNotAuthenticated() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> authService.getAuthenticatedUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No authenticated user found");
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenAnonymousUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        // When/Then
        assertThatThrownBy(() -> authService.getAuthenticatedUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No authenticated user found");
    }

    @Test
    void getAuthenticatedUser_ShouldThrowException_WhenPrincipalIsNotAuthenticationDetails() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object()); // Unknown principal type

        // When/Then
        assertThatThrownBy(() -> authService.getAuthenticatedUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Authenticated user is not of expected type");
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUserDto_WhenAuthenticatedWithJwt() {
        // Given
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("test@example.com");
        JwtAuthDetails authDetails = new JwtAuthDetails(userDto);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(authDetails);

        // When
        UserResponseDto result = authService.getAuthenticatedUser();

        // Then
        assertThat(result).isEqualTo(userDto);
    }
}
