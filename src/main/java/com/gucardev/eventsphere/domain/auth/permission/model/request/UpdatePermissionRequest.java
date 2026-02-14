package com.gucardev.eventsphere.domain.auth.permission.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating an existing permission.")
public class UpdatePermissionRequest {

    @Size(min = 2, max = 100, message = "Action must be between 2 and 100 characters")
    @Schema(description = "Permission action (e.g., CREATE, READ, UPDATE, DELETE)", example = "CREATE")
    private String action;

    @Size(min = 2, max = 100, message = "Resource must be between 2 and 100 characters")
    @Schema(description = "Resource the permission applies to (e.g., USER, ROLE, PRODUCT)", example = "USER")
    private String resource;

    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    @Schema(description = "Human-readable permission name", example = "Create User")
    private String displayName;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Permission description", example = "Allows creating new users in the system")
    private String description;
}
