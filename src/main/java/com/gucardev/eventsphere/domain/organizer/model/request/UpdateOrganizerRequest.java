package com.gucardev.eventsphere.domain.organizer.model.request;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.URL;

public record UpdateOrganizerRequest(
    String organizationName,

    @URL(message = "Invalid URL format")
    String websiteUrl,

    @Email(message = "Invalid email format")
    String contactEmail
) {}
