package com.gucardev.eventsphere.domain.ticket.model.request;

import com.gucardev.eventsphere.domain.ticket.entity.TicketStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketRequest(
    @NotBlank(message = "Ticket code is required")
    String ticketCode,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    BigDecimal price,

    @NotNull(message = "Status is required")
    TicketStatus status,

    @NotNull(message = "Attendee ID is required")
    UUID attendeeId,

    @NotNull(message = "Event ID is required")
    UUID eventId
) {}
