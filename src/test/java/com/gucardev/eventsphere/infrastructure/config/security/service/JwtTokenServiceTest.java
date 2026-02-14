package com.gucardev.eventsphere.infrastructure.config.security.service;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.PasswordAuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "my-extremely-secure-and-long-secret-key-for-testing-purposes-only".getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_MINUTES = 60;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(SECRET_KEY, EXPIRATION_MINUTES);
    }

    @Test
    void generateToken_ShouldReturnNonEmptyString() {
        // Given
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("test@example.com");
        userDto.setName("Test");
        userDto.setSurname("User");
        userDto.setPassword("encodedPassword");

        // Create roles with permissions
        UserResponseDto.RoleDto role = new UserResponseDto.RoleDto();
        role.setName("USER");
        UserResponseDto.PermissionDto permission = new UserResponseDto.PermissionDto();
        permission.setResource("ROLE");
        permission.setAction("READ");
        role.setPermissions(Set.of(permission));
        userDto.setRoles(Set.of(role));

        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);

        // When
        String token = jwtTokenService.generateToken(authDetails);

        // Then
        assertThat(token).isNotBlank();
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("test@example.com");
        userDto.setPassword("encodedPassword");
        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);
        String token = jwtTokenService.generateToken(authDetails);

        // When
        boolean isValid = jwtTokenService.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.string";

        // When
        boolean isValid = jwtTokenService.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractUserDtoFromToken_ShouldReturnCorrectUserData() {
        // Given
        UUID userId = UUID.randomUUID();
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(userId);
        userDto.setEmail("test@example.com");
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setPassword("encodedPassword");

        // Create roles with permissions
        UserResponseDto.RoleDto userRole = new UserResponseDto.RoleDto();
        userRole.setName("USER");
        UserResponseDto.PermissionDto readPerm = new UserResponseDto.PermissionDto();
        readPerm.setResource("USER");
        readPerm.setAction("READ");
        UserResponseDto.PermissionDto writePerm = new UserResponseDto.PermissionDto();
        writePerm.setResource("USER");
        writePerm.setAction("WRITE");
        userRole.setPermissions(Set.of(readPerm, writePerm));
        userDto.setRoles(Set.of(userRole));

        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);
        String token = jwtTokenService.generateToken(authDetails);

        // When
        UserResponseDto extractedDto = jwtTokenService.extractUserDtoFromToken(token);

        // Then
        assertThat(extractedDto).isNotNull();
        assertThat(extractedDto.getId()).isEqualTo(userId);
        assertThat(extractedDto.getEmail()).isEqualTo("test@example.com");
        assertThat(extractedDto.getName()).isEqualTo("John");
        assertThat(extractedDto.getSurname()).isEqualTo("Doe");
        // Authorities should include ROLE_USER and permissions USER:READ, USER:WRITE
        assertThat(extractedDto.getAuthorities()).containsExactlyInAnyOrder("ROLE_USER", "USER:READ", "USER:WRITE");
    }

    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        // Given
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("email@example.com");
        userDto.setPassword("encodedPassword");
        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);
        String token = jwtTokenService.generateToken(authDetails);

        // When
        String email = jwtTokenService.getEmailFromToken(token);

        // Then
        assertThat(email).isEqualTo("email@example.com");
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenHasExpired() {
        // Given
        // Create a service with -1 minutes expiration (expired 1 minute ago)
        JwtTokenService expiredService = new JwtTokenService(SECRET_KEY, -1);
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail("test@example.com");
        userDto.setPassword("encodedPassword");
        PasswordAuthDetails authDetails = new PasswordAuthDetails(userDto);

        String expiredToken = expiredService.generateToken(authDetails);

        // When
        boolean isValid = jwtTokenService.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }
}
