package com.gucardev.eventsphere.domain.auth.user.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Filter request for searching and filtering users with pagination.")
public class UserFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by email (case-insensitive partial match)", example = "john@example.com")
    private String email;

    @Schema(description = "Filter by user's first name (case-insensitive partial match)", example = "John")
    private String name;

    @Schema(description = "Filter by user's surname (case-insensitive partial match)", example = "Doe")
    private String surname;

    @Schema(description = "Filter by phone number (partial match)", example = "+1234")
    private String phoneNumber;

    @Schema(description = "Filter by activation status", example = "true")
    private Boolean activated;
}
