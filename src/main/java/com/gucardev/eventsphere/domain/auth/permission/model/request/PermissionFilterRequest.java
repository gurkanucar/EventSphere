package com.gucardev.eventsphere.domain.auth.permission.model.request;

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
@Schema(description = "Filter request for searching and filtering permissions with pagination.")
public class PermissionFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by action (case-insensitive partial match)", example = "CREATE")
    @Size(max = 100, message = "Action filter cannot exceed 100 characters")
    private String action;

    @Schema(description = "Filter by resource (case-insensitive partial match)", example = "USER")
    @Size(max = 100, message = "Resource filter cannot exceed 100 characters")
    private String resource;

    @Schema(description = "Filter by display name (case-insensitive partial match)", example = "Create")
    @Size(max = 100, message = "Display name filter cannot exceed 100 characters")
    private String displayName;
}
