package com.gucardev.eventsphere.domain.shared.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@JsonPropertyOrder({
        "id",
        "createdAt",
        "createdBy",
        "updatedAt",
        "updatedBy",
        "deletedAt",
        "deletedBy"
})
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Schema(description = "Base Data Transfer Object containing common audit fields for all entities.")
public class BaseDto {

    @Schema(description = "The timestamp when the entity was created.", example = "2025-09-20T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "The timestamp when the entity was last modified.", example = "2025-09-20T18:45:15")
    private LocalDateTime updatedAt;

    @Schema(description = "The user or system that created the entity.", example = "admin")
    private String createdBy;

    @Schema(description = "The user or system that last modified the entity.", example = "system-update")
    private String updatedBy;

    @Schema(description = "The timestamp when the entity was deleted.", example = "2025-09-20T18:45:15")
    private String deletedAt;

    @Schema(description = "The user or system that deleted the entity.", example = "system-delete")
    private String deletedBy;

}