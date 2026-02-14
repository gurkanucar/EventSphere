package com.gucardev.eventsphere.domain.event.attendee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeResponseDto {
    private UUID id;
    private String preferences;
    private UUID userId;
}
