package com.gucardev.eventsphere.domain.auth.user.model.parameter;

import com.gucardev.eventsphere.domain.auth.user.model.request.UpdateUserRequest;

import java.util.UUID;

/**
 * Parameter wrapper for UpdateUserUseCase.
 * Combines the user ID from the path parameter with the update request body.
 */
public record UpdateUserUseCaseParam(
                UUID id,
                UpdateUserRequest request) {
}
