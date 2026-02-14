package com.gucardev.eventsphere.domain.ticket.repository;

import com.gucardev.eventsphere.domain.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends BaseJpaRepository<Ticket, UUID> {
}
