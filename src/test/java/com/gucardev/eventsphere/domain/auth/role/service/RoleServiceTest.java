package com.gucardev.eventsphere.domain.auth.role.service;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.mapper.RoleMapper;
import com.gucardev.eventsphere.domain.auth.role.model.dto.RoleResponseDto;
import com.gucardev.eventsphere.domain.auth.role.model.request.CreateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.UpdateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    void shouldCreateRole_whenValidRequest() {
        // Arrange
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .displayName("Administrator")
                .description("Full system access")
                .build();

        Role savedRole = new Role();
        savedRole.setId(UUID.randomUUID());
        savedRole.setName("ADMIN");
        savedRole.setDisplayName("Administrator");

        RoleResponseDto expectedDto = new RoleResponseDto();
        expectedDto.setId(savedRole.getId());
        expectedDto.setName("ADMIN");

        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(roleMapper.toDto(savedRole)).thenReturn(expectedDto);

        // Act
        RoleResponseDto result = roleService.createRole(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedRole.getId());
        assertThat(result.getName()).isEqualTo("ADMIN");

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toDto(savedRole);
    }

    @Test
    void shouldThrowException_whenRoleAlreadyExists() {
        // Arrange
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .displayName("Administrator")
                .build();

        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> roleService.createRole(request))
                .isInstanceOf(RuntimeException.class);

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldCreateRoleWithPermissions_whenPermissionIdsProvided() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        Set<UUID> permissionIds = Set.of(permissionId);

        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .displayName("Administrator")
                .permissionIds(permissionIds)
                .build();

        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setAction("CREATE");
        permission.setResource("USER");

        Role savedRole = new Role();
        savedRole.setId(UUID.randomUUID());
        savedRole.setName("ADMIN");
        savedRole.setPermissions(Set.of(permission));

        RoleResponseDto expectedDto = new RoleResponseDto();
        expectedDto.setId(savedRole.getId());

        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(permissionRepository.findByIdIn(permissionIds)).thenReturn(Set.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);
        when(roleMapper.toDto(savedRole)).thenReturn(expectedDto);

        // Act
        RoleResponseDto result = roleService.createRole(request);

        // Assert
        assertThat(result).isNotNull();
        verify(permissionRepository).findByIdIn(permissionIds);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void shouldUpdateRole_whenValidRequest() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .displayName("Super Administrator")
                .description("Updated description")
                .build();

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("ADMIN");
        existingRole.setDisplayName("Administrator");

        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setName("ADMIN");
        updatedRole.setDisplayName("Super Administrator");

        RoleResponseDto expectedDto = new RoleResponseDto();
        expectedDto.setId(roleId);
        expectedDto.setDisplayName("Super Administrator");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);
        when(roleMapper.toDto(updatedRole)).thenReturn(expectedDto);

        // Act
        RoleResponseDto result = roleService.updateRole(roleId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDisplayName()).isEqualTo("Super Administrator");

        verify(roleRepository).findById(roleId);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistentRole() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .displayName("New Name")
                .build();

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.updateRole(roleId, request))
                .isInstanceOf(RuntimeException.class);

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldGetRoleById_whenRoleExists() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        RoleResponseDto expectedDto = new RoleResponseDto();
        expectedDto.setId(roleId);
        expectedDto.setName("ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(expectedDto);

        // Act
        RoleResponseDto result = roleService.getRoleById(roleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roleId);
        assertThat(result.getName()).isEqualTo("ADMIN");

        verify(roleRepository).findById(roleId);
        verify(roleMapper).toDto(role);
    }

    @Test
    void shouldThrowException_whenRoleNotFound() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.getRoleById(roleId))
                .isInstanceOf(RuntimeException.class);

        verify(roleRepository).findById(roleId);
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void shouldDeleteRole_whenRoleExists() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        // Act
        roleService.deleteRole(roleId);

        // Assert
        verify(roleRepository).findById(roleId);
        verify(roleRepository).delete(role);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistentRole() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roleService.deleteRole(roleId))
                .isInstanceOf(RuntimeException.class);

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).delete((Role) any());
    }
}
