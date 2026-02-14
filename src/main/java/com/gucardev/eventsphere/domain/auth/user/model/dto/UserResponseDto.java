package com.gucardev.eventsphere.domain.auth.user.model.dto; // Or your .dto package

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    // --- User Fields ---
    private UUID id;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;

    @JsonIgnore
    private String password;

    private Boolean activated;

    private Set<RoleDto> roles;

    private Set<String> authorities;

    @Getter
    @Setter
    public static class RoleDto {
        private UUID id;
        private String name;
        private String displayName;
        private String description;
        private Set<PermissionDto> permissions;
    }

    @Getter
    @Setter
    public static class PermissionDto {
        private UUID id;
        private String action;
        private String resource;
        private String displayName;
        private String description;
    }
}