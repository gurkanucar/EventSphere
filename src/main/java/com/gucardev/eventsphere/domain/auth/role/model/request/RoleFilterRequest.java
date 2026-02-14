package com.gucardev.eventsphere.domain.auth.role.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Filter request for searching and filtering roles with pagination.")
public class RoleFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by role name (case-insensitive partial match)", example = "ADMIN")
    @Size(max = 100, message = "Role name filter cannot exceed 100 characters")
    private String name;

    @Schema(description = "Filter by display name (case-insensitive partial match)", example = "Administrator")
    @Size(max = 100, message = "Display name filter cannot exceed 100 characters")
    private String displayName;
}
