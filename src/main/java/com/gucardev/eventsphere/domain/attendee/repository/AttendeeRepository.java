package com.gucardev.eventsphere.domain.attendee.repository;

import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttendeeRepository extends BaseJpaRepository<Attendee, UUID> {
}
