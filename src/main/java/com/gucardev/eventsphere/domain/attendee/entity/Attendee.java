package com.gucardev.eventsphere.domain.attendee.entity;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "attendees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendee extends BaseEntity {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    // Specific details for participants
    private String preferences; // e.g., "Vegetarian", "Wheelchair access needed"

    // Link to the Auth User (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Relationship: An attendee has many tickets
    @OneToMany(mappedBy = "attendee")
    @Builder.Default
    private Set<Ticket> tickets = new HashSet<>();

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setAttendee(this);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setAttendee(null);
    }
}
