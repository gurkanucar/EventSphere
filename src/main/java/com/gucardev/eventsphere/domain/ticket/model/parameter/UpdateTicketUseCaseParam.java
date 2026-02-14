package com.gucardev.eventsphere.domain.ticket.model.parameter;

import com.gucardev.eventsphere.domain.ticket.model.request.UpdateTicketRequest;
import java.util.UUID;

public record UpdateTicketUseCaseParam(UUID id, UpdateTicketRequest request) {
}
