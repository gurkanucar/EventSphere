package com.gucardev.eventsphere.domain.event.organizer.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import java.util.UUID;

public record CreateOrganizerRequest(
    @NotBlank(message = "Organization name is required")
    String organizationName,

    @URL(message = "Invalid URL format")
    String websiteUrl,

    @Email(message = "Invalid email format")
    String contactEmail,

    @NotNull(message = "User ID is required")
    UUID userId
) {}
