package com.gucardev.eventsphere.domain.school.student.service;

import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.mapper.StudentMapper;
import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.repository.StudentRepository;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllStudentsUseCase implements UseCase<Void, List<StudentResponseDto>> {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public List<StudentResponseDto> execute(Void input) {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(studentMapper::toDto)
                .toList();
    }
}
