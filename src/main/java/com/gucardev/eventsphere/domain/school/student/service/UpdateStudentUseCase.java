package com.gucardev.eventsphere.domain.school.student.service;

import com.gucardev.eventsphere.domain.school.classroom.entity.Classroom;
import com.gucardev.eventsphere.domain.school.classroom.repository.ClassroomRepository;
import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.mapper.StudentMapper;
import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.model.parameter.UpdateStudentParam;
import com.gucardev.eventsphere.domain.school.student.repository.StudentRepository;
import com.gucardev.eventsphere.domain.shared.util.ResourceOwnershipValidator;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updates a student profile.
 * IDOR Protection: Only the student themselves or an admin can update the
 * profile.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateStudentUseCase implements UseCase<UpdateStudentParam, StudentResponseDto> {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentMapper studentMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public StudentResponseDto execute(UpdateStudentParam param) {
        Student student = studentRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Student", param.id()));

        // IDOR Protection: Verify the authenticated user owns this student profile or
        // is an admin
        ownershipValidator.validateOwnership(student.getUser().getId());

        // Update student fields
        studentMapper.updateEntityFromRequest(param.request(), student);

        // Update classroom if provided
        if (param.request().getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(param.request().getClassroomId())
                    .orElseThrow(() -> ExceptionUtil.notFound("Classroom", param.request().getClassroomId()));
            student.setClassroom(classroom);
        }

        Student updatedStudent = studentRepository.save(student);
        log.info("Updated student profile: {} by user: {}",
                updatedStudent.getSchoolNumber(),
                ownershipValidator.getCurrentUserId());

        return studentMapper.toDto(updatedStudent);
    }
}
