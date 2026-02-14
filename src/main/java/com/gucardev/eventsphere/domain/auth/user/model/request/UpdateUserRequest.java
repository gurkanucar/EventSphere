package com.gucardev.eventsphere.domain.auth.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating an existing user.")
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "User's password (minimum 8 characters)", example = "SecurePass123!")
    private String password;

    @Schema(description = "User's first name", example = "John")
    private String name;

    @Schema(description = "User's surname", example = "Doe")
    private String surname;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Whether the user account is activated", example = "true")
    private Boolean activated;

    @Schema(description = "Set of role IDs to assign to the user", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
    private Set<UUID> roleIds;
}
