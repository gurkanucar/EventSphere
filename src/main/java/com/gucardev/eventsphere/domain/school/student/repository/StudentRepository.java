package com.gucardev.eventsphere.domain.school.student.repository;

import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends BaseJpaRepository<Student, UUID> {

    @EntityGraph(attributePaths = { "user", "classroom" })
    Optional<Student> findById(UUID id);

    @EntityGraph(attributePaths = { "user" })
    Optional<Student> findBySchoolNumber(String schoolNumber);

    @EntityGraph(attributePaths = { "user" })
    Optional<Student> findByUserId(UUID userId);

    boolean existsBySchoolNumber(String schoolNumber);

    boolean existsByUserId(UUID userId);
}
