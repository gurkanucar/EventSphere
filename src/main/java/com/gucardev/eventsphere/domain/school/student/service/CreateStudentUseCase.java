package com.gucardev.eventsphere.domain.school.student.service;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.domain.school.classroom.entity.Classroom;
import com.gucardev.eventsphere.domain.school.classroom.repository.ClassroomRepository;
import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.mapper.StudentMapper;
import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.model.request.CreateStudentRequest;
import com.gucardev.eventsphere.domain.school.student.repository.StudentRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Creates a new student with associated user account.
 * This UseCase demonstrates the Identity vs Profile Separation pattern:
 * 1. Creates a User (identity/authentication)
 * 2. Assigns ROLE_STUDENT
 * 3. Creates Student profile linked to the User
 * All operations are atomic within a single transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateStudentUseCase implements UseCase<CreateStudentRequest, StudentResponseDto> {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponseDto execute(CreateStudentRequest request) {
        // 1. Validation
        validateRequest(request);

        // 2. Create User (Identity)
        User user = createUser(request);

        // 3. Assign ROLE_STUDENT
        assignStudentRole(user);

        // 4. Save User
        User savedUser = userRepository.save(user);
        log.info("Created user account for student: {}", savedUser.getEmail());

        // 5. Create Student Profile
        Student student = createStudentProfile(savedUser, request);

        // 6. Link to Classroom (if provided)
        linkToClassroom(student, request);

        // 7. Save Student
        Student savedStudent = studentRepository.save(student);
        log.info("Created student profile: {} for user: {}",
                savedStudent.getSchoolNumber(), savedUser.getEmail());

        return studentMapper.toDto(savedStudent);
    }

    private void validateRequest(CreateStudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ExceptionUtil.alreadyExists("User", "email", request.getEmail());
        }

        if (studentRepository.existsBySchoolNumber(request.getSchoolNumber())) {
            throw ExceptionUtil.alreadyExists("Student", "schoolNumber", request.getSchoolNumber());
        }
    }

    private User createUser(CreateStudentRequest request) {
        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .surname(request.getSurname())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(
                        StringUtils.hasText(request.getPassword())
                                ? request.getPassword()
                                : generateDefaultPassword(request.getSchoolNumber())))
                .activated(true)
                .build();
    }

    private void assignStudentRole(User user) {
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> ExceptionUtil.notFound("Role", "ROLE_STUDENT"));
        user.addRole(studentRole);
    }

    private Student createStudentProfile(User user, CreateStudentRequest request) {
        return Student.builder()
                .user(user)
                .schoolNumber(request.getSchoolNumber())
                .enrollmentDate(request.getEnrollmentDate() != null
                        ? request.getEnrollmentDate()
                        : LocalDate.now())
                .gradeLevel(request.getGradeLevel())
                .build();
    }

    private void linkToClassroom(Student student, CreateStudentRequest request) {
        if (request.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> ExceptionUtil.notFound("Classroom", request.getClassroomId()));
            student.setClassroom(classroom);
        }
    }

    /**
     * Generates a default password: school number + current year
     * Example: 2024001 -> 20240012024
     */
    private String generateDefaultPassword(String schoolNumber) {
        return schoolNumber + LocalDate.now().getYear();
    }
}
