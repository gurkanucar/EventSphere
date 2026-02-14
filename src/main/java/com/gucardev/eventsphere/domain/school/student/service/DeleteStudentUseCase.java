package com.gucardev.eventsphere.domain.school.student.service;

import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.repository.StudentRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCaseWithInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Soft deletes a student.
 * Note: This only soft-deletes the Student profile, not the User account.
 * The User can still login if they have other roles (e.g., ROLE_PARENT).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteStudentUseCase implements UseCaseWithInput<UUID> {

    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Student", id));

        studentRepository.softDelete(id, "Student profile deleted");
        log.info("Soft deleted student: {}", student.getSchoolNumber());
    }
}
