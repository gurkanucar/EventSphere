package com.gucardev.eventsphere.domain.ticket.service.usecase;

import com.gucardev.eventsphere.domain.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.ticket.mapper.TicketMapper;
import com.gucardev.eventsphere.domain.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.ticket.model.parameter.UpdateTicketUseCaseParam;
import com.gucardev.eventsphere.domain.ticket.repository.TicketRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTicketUseCase implements UseCase<UpdateTicketUseCaseParam, TicketResponseDto> {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponseDto execute(UpdateTicketUseCaseParam param) {
        Ticket ticket = ticketRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Ticket", param.id()));

        // Ownership check can be added here if needed, keeping it simple for now.

        ticketMapper.updateEntityFromRequest(param.request(), ticket);

        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Updated ticket: {}", updatedTicket.getId());

        return ticketMapper.toDto(updatedTicket);
    }
}
