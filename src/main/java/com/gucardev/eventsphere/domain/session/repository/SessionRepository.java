package com.gucardev.eventsphere.domain.session.repository;

import com.gucardev.eventsphere.domain.session.entity.Session;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepository extends BaseJpaRepository<Session, UUID> {
}
