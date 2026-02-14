package com.gucardev.eventsphere.domain.event.event.model.request;

import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

public record UpdateEventRequest(
    String title,
    String description,
    @Future(message = "Start time must be in the future")
    LocalDateTime startTime,
    @Future(message = "End time must be in the future")
    LocalDateTime endTime,
    String location,
    Boolean isPublished
) {}
