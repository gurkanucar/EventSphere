package com.gucardev.eventsphere.domain.school.classroom.repository;

import com.gucardev.eventsphere.domain.school.classroom.entity.Classroom;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassroomRepository extends BaseJpaRepository<Classroom, UUID> {

    boolean existsByName(String name);
}
