package com.gucardev.eventsphere.domain.ticket.repository.specification;

import com.gucardev.eventsphere.domain.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.ticket.entity.TicketStatus;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TicketSpecification extends BaseSpecification {

    public static Specification<Ticket> withTicketCode(String ticketCode) {
        return BaseSpecification.like("ticketCode", ticketCode);
    }

    public static Specification<Ticket> withStatus(TicketStatus status) {
        return BaseSpecification.equals("status", status);
    }

    public static Specification<Ticket> withEventId(UUID eventId) {
        return (root, query, cb) -> {
            if (eventId == null) return null;
            return cb.equal(root.get("event").get("id"), eventId);
        };
    }

    public static Specification<Ticket> withAttendeeId(UUID attendeeId) {
        return (root, query, cb) -> {
            if (attendeeId == null) return null;
            return cb.equal(root.get("attendee").get("id"), attendeeId);
        };
    }

    public static Specification<Ticket> fetchEventAndAttendee() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("event", JoinType.LEFT);
                root.fetch("attendee", JoinType.LEFT);
            }
            return null;
        };
    }
}
