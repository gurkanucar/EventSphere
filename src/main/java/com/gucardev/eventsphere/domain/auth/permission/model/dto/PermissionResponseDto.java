package com.gucardev.eventsphere.domain.auth.permission.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Permission response DTO")
public class PermissionResponseDto {

    @Schema(description = "Permission unique identifier", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Permission action", example = "CREATE")
    private String action;

    @Schema(description = "Resource the permission applies to", example = "USER")
    private String resource;

    @Schema(description = "Human-readable permission name", example = "Create User")
    private String displayName;

    @Schema(description = "Permission description", example = "Allows creating new users")
    private String description;
}
