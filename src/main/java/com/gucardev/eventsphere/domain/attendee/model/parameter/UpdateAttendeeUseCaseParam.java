package com.gucardev.eventsphere.domain.attendee.model.parameter;

import com.gucardev.eventsphere.domain.attendee.model.request.UpdateAttendeeRequest;
import java.util.UUID;

public record UpdateAttendeeUseCaseParam(UUID id, UpdateAttendeeRequest request) {
}
