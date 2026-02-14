package com.gucardev.eventsphere.domain.organizer.model.parameter;

import com.gucardev.eventsphere.domain.organizer.model.request.UpdateOrganizerRequest;
import java.util.UUID;

public record UpdateOrganizerUseCaseParam(UUID id, UpdateOrganizerRequest request) {
}
