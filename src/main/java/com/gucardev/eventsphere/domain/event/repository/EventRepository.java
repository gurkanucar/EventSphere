package com.gucardev.eventsphere.domain.event.repository;

import com.gucardev.eventsphere.domain.event.entity.Event;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends BaseJpaRepository<Event, UUID> {
}
