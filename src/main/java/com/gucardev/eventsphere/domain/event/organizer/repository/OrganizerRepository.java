package com.gucardev.eventsphere.domain.event.organizer.repository;

import com.gucardev.eventsphere.domain.event.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizerRepository extends BaseJpaRepository<Organizer, UUID> {
}
