package com.gucardev.eventsphere.domain.attendee.model.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateAttendeeRequest(
    String preferences,

    @NotNull(message = "User ID is required")
    UUID userId
) {}
