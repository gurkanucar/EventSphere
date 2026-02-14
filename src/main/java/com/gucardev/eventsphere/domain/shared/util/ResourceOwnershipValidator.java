package com.gucardev.eventsphere.domain.shared.util;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.service.AuthService;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionType;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Reusable utility for validating resource ownership.
 * Prevents IDOR (Insecure Direct Object Reference) vulnerabilities.
 */
@Component
@RequiredArgsConstructor
public class ResourceOwnershipValidator {

    private final AuthService authService;

    /**
     * Verifies the current user owns the resource or has ADMIN role.
     * 
     * @param resourceOwnerId UUID of the resource owner
     * @throws com.gucardev.eventsphere.infrastructure.exception.CustomException if
     *                                                                         the
     *                                                                         user
     *                                                                         is
     *                                                                         not
     *                                                                         the
     *                                                                         owner
     *                                                                         and
     *                                                                         not
     *                                                                         an
     *                                                                         admin
     */
    public void validateOwnership(UUID resourceOwnerId) {
        UserResponseDto currentUser = authService.getAuthenticatedUser();
        boolean isOwner = currentUser.getId().equals(resourceOwnerId);
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw ExceptionUtil.of(ExceptionType.FORBIDDEN, "You do not have permission to access this resource");
        }
    }

    /**
     * Strict ownership check — admins are NOT exempt.
     * Use for sensitive personal operations (e.g., change own password).
     */
    public void validateStrictOwnership(UUID resourceOwnerId) {
        UserResponseDto currentUser = authService.getAuthenticatedUser();
        if (!currentUser.getId().equals(resourceOwnerId)) {
            throw ExceptionUtil.of(ExceptionType.FORBIDDEN, "You do not have permission to access this resource");
        }
    }

    /**
     * Gets the current authenticated user's ID.
     * Useful for filtering queries by ownership.
     */
    public UUID getCurrentUserId() {
        return authService.getAuthenticatedUser().getId();
    }
}
