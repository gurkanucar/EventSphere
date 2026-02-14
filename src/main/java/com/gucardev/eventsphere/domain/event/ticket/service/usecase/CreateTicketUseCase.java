package com.gucardev.eventsphere.domain.event.ticket.service.usecase;

import com.gucardev.eventsphere.domain.event.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.event.attendee.repository.AttendeeRepository;
import com.gucardev.eventsphere.domain.event.event.entity.Event;
import com.gucardev.eventsphere.domain.event.event.repository.EventRepository;
import com.gucardev.eventsphere.domain.event.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.event.ticket.mapper.TicketMapper;
import com.gucardev.eventsphere.domain.event.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.event.ticket.model.request.CreateTicketRequest;
import com.gucardev.eventsphere.domain.event.ticket.repository.TicketRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTicketUseCase implements UseCase<CreateTicketRequest, TicketResponseDto> {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponseDto execute(CreateTicketRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> ExceptionUtil.notFound("Event", request.eventId()));

        Attendee attendee = attendeeRepository.findById(request.attendeeId())
                .orElseThrow(() -> ExceptionUtil.notFound("Attendee", request.attendeeId()));

        // Logic check: ensure ticket code is unique or handled by DB constraint
        // (DB has unique constraint on ticketCode)

        Ticket ticket = ticketMapper.toEntity(request);
        ticket.setEvent(event);
        ticket.setAttendee(attendee);
        
        // Add to relationships for consistency
        event.addTicket(ticket);
        attendee.addTicket(ticket);

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Created ticket: {}", savedTicket.getId());

        return ticketMapper.toDto(savedTicket);
    }
}
