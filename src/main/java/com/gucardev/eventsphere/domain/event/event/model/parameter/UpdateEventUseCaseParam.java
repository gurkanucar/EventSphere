package com.gucardev.eventsphere.domain.event.event.model.parameter;

import com.gucardev.eventsphere.domain.event.event.model.request.UpdateEventRequest;
import java.util.UUID;

public record UpdateEventUseCaseParam(UUID id, UpdateEventRequest request) {
}
