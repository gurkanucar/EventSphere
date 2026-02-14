package com.gucardev.eventsphere.domain.school.student.controller;

import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.model.parameter.UpdateStudentParam;
import com.gucardev.eventsphere.domain.school.student.model.request.CreateStudentRequest;
import com.gucardev.eventsphere.domain.school.student.model.request.UpdateStudentRequest;
import com.gucardev.eventsphere.domain.school.student.service.*;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student profile management endpoints")
public class StudentController {

        private final CreateStudentUseCase createStudentUseCase;
        private final GetStudentByIdUseCase getStudentByIdUseCase;
        private final GetAllStudentsUseCase getAllStudentsUseCase;
        private final UpdateStudentUseCase updateStudentUseCase;
        private final DeleteStudentUseCase deleteStudentUseCase;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Create a new student", description = "Creates a new student with associated user account. Only admins can create students. "
                        +
                        "This operation creates both a User (identity) and Student profile (domain) atomically.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Student created successfully", content = @Content(schema = @Schema(implementation = StudentResponseWrapper.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "409", description = "Email or school number already exists")
        })
        public ResponseEntity<ApiResponseWrapper<StudentResponseDto>> createStudent(
                        @Valid @RequestBody CreateStudentRequest request) {
                StudentResponseDto student = createStudentUseCase.execute(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseWrapper.success(student));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
        @Operation(summary = "Get student by ID", description = "Retrieves a student profile by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Student found", content = @Content(schema = @Schema(implementation = StudentResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "Student not found")
        })
        public ResponseEntity<ApiResponseWrapper<StudentResponseDto>> getStudentById(
                        @PathVariable UUID id) {
                StudentResponseDto student = getStudentByIdUseCase.execute(id);
                return ResponseEntity.ok(ApiResponseWrapper.success(student));
        }

        @GetMapping("/all")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        @Operation(summary = "Get all students", description = "Retrieves all students without pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Students retrieved successfully", content = @Content(schema = @Schema(implementation = StudentListResponseWrapper.class)))
        })
        public ResponseEntity<ApiResponseWrapper<List<StudentResponseDto>>> getAllStudents() {
                List<StudentResponseDto> students = getAllStudentsUseCase.execute(null);
                return ResponseEntity.ok(ApiResponseWrapper.success(students));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
        @Operation(summary = "Update student", description = "Updates a student profile. Students can only update their own profile, admins can update any student.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Student updated successfully", content = @Content(schema = @Schema(implementation = StudentResponseWrapper.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied - not the owner or admin"),
                        @ApiResponse(responseCode = "404", description = "Student not found")
        })
        public ResponseEntity<ApiResponseWrapper<StudentResponseDto>> updateStudent(
                        @PathVariable UUID id,
                        @Valid @RequestBody UpdateStudentRequest request) {
                UpdateStudentParam param = new UpdateStudentParam(id, request);
                StudentResponseDto student = updateStudentUseCase.execute(param);
                return ResponseEntity.ok(ApiResponseWrapper.success(student));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Delete student", description = "Soft deletes a student profile. The associated User account is preserved.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Student deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Student not found")
        })
        public ResponseEntity<ApiResponseWrapper<Object>> deleteStudent(@PathVariable UUID id) {
                deleteStudentUseCase.execute(id);
                return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
        }

        // Swagger documentation helper classes
        @Schema(description = "Student response wrapper")
        private static class StudentResponseWrapper extends ApiResponseWrapper<StudentResponseDto> {
        }

        @Schema(description = "Student list response wrapper")
        private static class StudentListResponseWrapper extends ApiResponseWrapper<List<StudentResponseDto>> {
        }
}
