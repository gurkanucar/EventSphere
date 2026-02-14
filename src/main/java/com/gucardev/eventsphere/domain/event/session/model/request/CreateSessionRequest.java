package com.gucardev.eventsphere.domain.event.session.model.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateSessionRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    LocalDateTime endTime,

    String speakerName,

    @NotNull(message = "Event ID is required")
    UUID eventId
) {}
