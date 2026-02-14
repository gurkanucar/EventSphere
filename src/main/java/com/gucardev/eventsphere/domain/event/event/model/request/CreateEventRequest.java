package com.gucardev.eventsphere.domain.event.event.model.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    LocalDateTime endTime,

    @NotBlank(message = "Location is required")
    String location,

    @NotNull(message = "Organizer ID is required")
    UUID organizerId
) {}
