package com.gucardev.eventsphere.domain.school.student.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Request to update an existing student")
public class UpdateStudentRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "Student first name", example = "Ahmet")
    private String name;

    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    @Schema(description = "Student last name", example = "Yılmaz")
    private String surname;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Schema(description = "Phone number", example = "+905551234567")
    private String phoneNumber;

    @Schema(description = "Classroom ID")
    private UUID classroomId;

    @Schema(description = "Enrollment date", example = "2024-09-01")
    private LocalDate enrollmentDate;

    @Min(value = 1, message = "Grade level must be at least 1")
    @Max(value = 12, message = "Grade level must not exceed 12")
    @Schema(description = "Grade level (1-12)", example = "9")
    private Integer gradeLevel;
}
