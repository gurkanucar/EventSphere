package com.gucardev.eventsphere.domain.ticket.model.request;

import com.gucardev.eventsphere.domain.ticket.entity.TicketStatus;
import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

import java.util.UUID;

@Getter
@Setter
@ParameterObject
public class TicketFilterRequest extends BaseFilterRequest {
    private String ticketCode;
    private TicketStatus status;
    private UUID eventId;
    private UUID attendeeId;
}
