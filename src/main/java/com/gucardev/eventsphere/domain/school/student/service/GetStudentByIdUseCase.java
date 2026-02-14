package com.gucardev.eventsphere.domain.school.student.service;

import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.mapper.StudentMapper;
import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.repository.StudentRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetStudentByIdUseCase implements UseCase<UUID, StudentResponseDto> {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDto execute(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Student", id));

        return studentMapper.toDto(student);
    }
}
