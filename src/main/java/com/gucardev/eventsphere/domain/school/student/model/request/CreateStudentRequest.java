package com.gucardev.eventsphere.domain.school.student.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
@Schema(description = "Request to create a new student")
public class CreateStudentRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Student email", example = "student@school.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "Student first name", example = "Ahmet", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    @Schema(description = "Student last name", example = "Yılmaz", requiredMode = Schema.RequiredMode.REQUIRED)
    private String surname;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Schema(description = "Phone number", example = "+905551234567")
    private String phoneNumber;

    @NotBlank(message = "School number is required")
    @Size(min = 4, max = 20, message = "School number must be between 4 and 20 characters")
    @Schema(description = "Unique school number", example = "2024001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String schoolNumber;

    @Schema(description = "Classroom ID")
    private UUID classroomId;

    @Schema(description = "Enrollment date (defaults to today if not provided)", example = "2024-09-01")
    private LocalDate enrollmentDate;

    @NotNull(message = "Grade level is required")
    @Min(value = 1, message = "Grade level must be at least 1")
    @Max(value = 12, message = "Grade level must not exceed 12")
    @Schema(description = "Grade level (1-12)", example = "9", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer gradeLevel;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "Initial password (optional, will be auto-generated if not provided)", example = "SecurePass123")
    private String password;
}
