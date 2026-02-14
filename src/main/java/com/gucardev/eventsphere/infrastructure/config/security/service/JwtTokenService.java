package com.gucardev.eventsphere.infrastructure.config.security.service;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.PasswordAuthDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenService {

    private static final String CLAIM_ID = "id";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_SURNAME = "surname";
    private static final String CLAIM_PHONE_NUMBER = "phoneNumber";
    private static final String CLAIM_ACTIVATED = "activated";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String CLAIM_EMAIL = "email";

    private final SecretKey signingKey;
    private final long jwtTokenExpiresInMinutes;
    private final JwtParser jwtParser;

    public JwtTokenService(
            @Value("${app-specific-configs.security.jwt.secret-key}") String secretKey,
            @Value("${app-specific-configs.security.jwt.token-validity-in-minutes}") long jwtTokenExpiresInMinutes) {

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtTokenExpiresInMinutes = jwtTokenExpiresInMinutes;
        this.jwtParser = Jwts.parser().verifyWith(this.signingKey).build();
    }

    public String generateToken(PasswordAuthDetails authDetails) {
        UserResponseDto userDto = authDetails.getUserDto();
        Instant now = Instant.now();

        // Extract role names: ["ADMIN", "USER_MANAGER"]
        List<String> roleNames = extractRoleNames(userDto.getRoles());

        // Extract permissions: ["USER:READ", "USER:WRITE"]
        List<String> permissionStrings = extractPermissionStrings(userDto.getRoles());

        return Jwts.builder()
                .subject(userDto.getEmail())
                .claim(CLAIM_EMAIL, userDto.getEmail())
                .claim(CLAIM_ID, userDto.getId().toString())
                .claim(CLAIM_NAME, userDto.getName())
                .claim(CLAIM_SURNAME, userDto.getSurname())
                .claim(CLAIM_PHONE_NUMBER, userDto.getPhoneNumber())
                .claim(CLAIM_ACTIVATED, userDto.getActivated())
                .claim(CLAIM_ROLES, roleNames)
                .claim(CLAIM_PERMISSIONS, permissionStrings)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtTokenExpiresInMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT Token: {}", ex.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public UserResponseDto extractUserDtoFromToken(String token) {
        Claims claims = getClaims(token);

        String idStr = claims.get(CLAIM_ID, String.class);
        if (idStr == null) {
            throw new JwtException("Token is missing required 'id' claim");
        }

        // Reconstruct authorities from roles + permissions for Spring Security
        Set<String> authorities = buildAuthorities(claims);

        return UserResponseDto.builder()
                .id(parseUUID(idStr))
                .email(claims.getSubject())
                .name(claims.get(CLAIM_NAME, String.class))
                .surname(claims.get(CLAIM_SURNAME, String.class))
                .phoneNumber(claims.get(CLAIM_PHONE_NUMBER, String.class))
                .activated(claims.get(CLAIM_ACTIVATED, Boolean.class))
                .authorities(authorities)
                .roles(null) // Keep roles null, not needed in DTO after extraction
                .build();
    }

    private Claims getClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    /**
     * Build authorities set from roles and permissions in token.
     * Combines: ROLE_* prefixed role names + permission strings
     */
    private Set<String> buildAuthorities(Claims claims) {
        Set<String> authorities = new HashSet<>();

        // Add roles with ROLE_ prefix
        List<?> roles = claims.get(CLAIM_ROLES, List.class);
        if (roles != null) {
            for (Object role : roles) {
                if (role instanceof String) {
                    authorities.add("ROLE_" + role);
                }
            }
        }

        // Add permissions as-is
        List<?> permissions = claims.get(CLAIM_PERMISSIONS, List.class);
        if (permissions != null) {
            for (Object perm : permissions) {
                if (perm instanceof String) {
                    authorities.add((String) perm);
                }
            }
        }

        return authorities;
    }

    /**
     * Extract role names: ["ADMIN", "USER_MANAGER"]
     */
    private List<String> extractRoleNames(Set<UserResponseDto.RoleDto> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(UserResponseDto.RoleDto::getName)
                .collect(Collectors.toList());
    }

    /**
     * Extract permissions using flatMap: ["USER:READ", "USER:WRITE"]
     */
    private List<String> extractPermissionStrings(Set<UserResponseDto.RoleDto> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(role -> role.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .map(perm -> perm.getResource() + ":" + perm.getAction())
                .distinct()
                .collect(Collectors.toList());
    }

    private UUID parseUUID(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Token contains invalid UUID format", e);
        }
    }
}