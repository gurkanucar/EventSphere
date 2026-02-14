package com.gucardev.eventsphere.domain.auth.role.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request object for creating a new role.")
public class CreateRoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @Schema(description = "Role name (ROLE_ prefix will be added automatically if not present)", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    @Schema(description = "Human-readable role name", example = "Administrator", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Role description", example = "Full system access with all permissions")
    private String description;

    @Schema(description = "Set of permission IDs to assign to this role", example = "[\"550e8400-e29b-41d4-a716-446655440001\"]")
    private Set<UUID> permissionIds;
}
