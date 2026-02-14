package com.gucardev.eventsphere.domain.event.attendee.model.parameter;

import com.gucardev.eventsphere.domain.event.attendee.model.request.UpdateAttendeeRequest;
import java.util.UUID;

public record UpdateAttendeeUseCaseParam(UUID id, UpdateAttendeeRequest request) {
}
