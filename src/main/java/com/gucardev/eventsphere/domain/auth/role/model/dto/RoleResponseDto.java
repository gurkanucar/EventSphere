package com.gucardev.eventsphere.domain.auth.role.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Role response DTO")
public class RoleResponseDto {

    @Schema(description = "Role unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Role name (without ROLE_ prefix)", example = "ADMIN")
    private String name;

    @Schema(description = "Human-readable role name", example = "Administrator")
    private String displayName;

    @Schema(description = "Role description", example = "Full system access")
    private String description;

    @Schema(description = "Set of permissions assigned to this role")
    private Set<PermissionDto> permissions;

    @Getter
    @Setter
    @Schema(description = "Permission DTO")
    public static class PermissionDto {

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
}
