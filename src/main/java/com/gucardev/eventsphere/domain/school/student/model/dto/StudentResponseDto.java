package com.gucardev.eventsphere.domain.school.student.model.dto;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student profile response")
public class StudentResponseDto {

    @Schema(description = "Student ID", accessMode = Schema.AccessMode.READ_ONLY, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Associated user information")
    private UserResponseDto user;

    @Schema(description = "School number", example = "2024001")
    private String schoolNumber;

    @Schema(description = "Classroom name", example = "9-A")
    private String classroomName;

    @Schema(description = "Classroom ID")
    private UUID classroomId;

    @Schema(description = "Enrollment date", example = "2024-09-01")
    private LocalDate enrollmentDate;

    @Schema(description = "Grade level", example = "9")
    private Integer gradeLevel;

    @Schema(description = "Number of parents/guardians", example = "2")
    private Integer parentsCount;
}
