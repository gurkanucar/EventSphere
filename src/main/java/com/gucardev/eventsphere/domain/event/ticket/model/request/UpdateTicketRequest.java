package com.gucardev.eventsphere.domain.event.ticket.model.request;

import com.gucardev.eventsphere.domain.event.ticket.entity.TicketStatus;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record UpdateTicketRequest(
    String ticketCode,

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    BigDecimal price,

    TicketStatus status
) {}
