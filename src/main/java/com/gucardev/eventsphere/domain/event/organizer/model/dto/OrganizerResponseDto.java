package com.gucardev.eventsphere.domain.event.organizer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerResponseDto {
    private UUID id;
    private String organizationName;
    private String websiteUrl;
    private String contactEmail;
    private UUID userId;
}
