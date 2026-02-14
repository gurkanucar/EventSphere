package com.gucardev.eventsphere.domain.event.ticket.model.dto;

import com.gucardev.eventsphere.domain.event.ticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {
    private UUID id;
    private String ticketCode;
    private BigDecimal price;
    private TicketStatus status;
    private UUID eventId;
    private UUID attendeeId;
}
