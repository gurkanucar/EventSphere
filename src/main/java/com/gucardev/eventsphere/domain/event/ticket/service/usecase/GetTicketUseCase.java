package com.gucardev.eventsphere.domain.event.ticket.service.usecase;

import com.gucardev.eventsphere.domain.event.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.event.ticket.mapper.TicketMapper;
import com.gucardev.eventsphere.domain.event.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.event.ticket.repository.TicketRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTicketUseCase implements UseCase<UUID, TicketResponseDto> {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDto execute(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Ticket", id));
        return ticketMapper.toDto(ticket);
    }
}
