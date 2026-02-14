package com.gucardev.eventsphere.domain.auth.user.mapper;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import org.mapstruct.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "authorities", ignore = true) // Handled in AfterMapping
    UserResponseDto toUserResponseDto(User user);

    UserResponseDto.RoleDto toRoleDto(Role role);

    UserResponseDto.PermissionDto toPermissionDto(Permission permission);

    @AfterMapping
    default void populateAuthorities(User user, @MappingTarget UserResponseDto targetDto) {
        if (user == null || user.getRoles() == null) {
            targetDto.setAuthorities(Collections.emptySet());
            return;
        }

        // 1. Map Role Names (e.g., "ROLE_ADMIN")
        Set<String> roleAuthorities = user.getRoles().stream()
                .map(role -> Role.ROLE_PREFIX + role.getName())
                .collect(Collectors.toSet());

        // 2. Map Permissions (e.g., "USER:READ")
        Set<String> permissionAuthorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions() == null
                        ? Stream.empty()
                        : role.getPermissions().stream())
                .map(perm -> perm.getResource() + ":" + perm.getAction())
                .collect(Collectors.toSet());

        // 3. Combine them
        Set<String> allAuthorities = new HashSet<>();
        allAuthorities.addAll(roleAuthorities);
        allAuthorities.addAll(permissionAuthorities);

        targetDto.setAuthorities(allAuthorities);
    }
}