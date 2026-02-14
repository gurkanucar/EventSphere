package com.gucardev.eventsphere.domain.event.ticket.service.usecase;

import com.gucardev.eventsphere.domain.event.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.event.ticket.mapper.TicketMapper;
import com.gucardev.eventsphere.domain.event.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.event.ticket.model.request.TicketFilterRequest;
import com.gucardev.eventsphere.domain.event.ticket.repository.specification.TicketSpecification;
import com.gucardev.eventsphere.domain.event.ticket.repository.TicketRepository;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GetTicketListUseCase implements UseCase<TicketFilterRequest, Page<TicketResponseDto>> {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDto> execute(TicketFilterRequest filter) {
        Specification<Ticket> spec = BaseSpecification.toSpec(filter);

        if (StringUtils.hasText(filter.getTicketCode())) {
            spec = spec.and(TicketSpecification.withTicketCode(filter.getTicketCode()));
        }
        if (filter.getStatus() != null) {
            spec = spec.and(TicketSpecification.withStatus(filter.getStatus()));
        }
        if (filter.getEventId() != null) {
            spec = spec.and(TicketSpecification.withEventId(filter.getEventId()));
        }
        if (filter.getAttendeeId() != null) {
            spec = spec.and(TicketSpecification.withAttendeeId(filter.getAttendeeId()));
        }

        // Fetch event and attendee to avoid N+1
        spec = spec.and(TicketSpecification.fetchEventAndAttendee());

        Pageable pageable = filter.toPageable();
        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toDto);
    }
}
