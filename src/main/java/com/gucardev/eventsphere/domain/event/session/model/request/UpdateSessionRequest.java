package com.gucardev.eventsphere.domain.event.session.model.request;

import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

public record UpdateSessionRequest(
    String title,
    @Future(message = "Start time must be in the future")
    LocalDateTime startTime,
    @Future(message = "End time must be in the future")
    LocalDateTime endTime,
    String speakerName
) {}
