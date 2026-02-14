package com.gucardev.eventsphere.domain.event.organizer.model.parameter;

import com.gucardev.eventsphere.domain.event.organizer.model.request.UpdateOrganizerRequest;
import java.util.UUID;

public record UpdateOrganizerUseCaseParam(UUID id, UpdateOrganizerRequest request) {
}
