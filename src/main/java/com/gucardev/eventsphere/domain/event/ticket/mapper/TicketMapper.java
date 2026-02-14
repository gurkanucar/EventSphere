package com.gucardev.eventsphere.domain.event.ticket.mapper;

import com.gucardev.eventsphere.domain.event.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.event.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.event.ticket.model.request.CreateTicketRequest;
import com.gucardev.eventsphere.domain.event.ticket.model.request.UpdateTicketRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "attendeeId", source = "attendee.id")
    TicketResponseDto toDto(Ticket ticket);

    Ticket toEntity(CreateTicketRequest request);

    void updateEntityFromRequest(UpdateTicketRequest request, @MappingTarget Ticket ticket);
}
