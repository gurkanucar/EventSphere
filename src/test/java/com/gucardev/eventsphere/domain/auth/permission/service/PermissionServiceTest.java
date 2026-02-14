package com.gucardev.eventsphere.domain.auth.permission.service;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.mapper.PermissionMapper;
import com.gucardev.eventsphere.domain.auth.permission.model.dto.PermissionResponseDto;
import com.gucardev.eventsphere.domain.auth.permission.model.request.CreatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.UpdatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void shouldCreatePermission_whenValidRequest() {
        // Arrange
        CreatePermissionRequest request = CreatePermissionRequest.builder()
                .action("CREATE")
                .resource("USER")
                .displayName("Create User")
                .description("Allows creating new users")
                .build();

        Permission savedPermission = new Permission();
        savedPermission.setId(UUID.randomUUID());
        savedPermission.setAction("CREATE");
        savedPermission.setResource("USER");

        PermissionResponseDto expectedDto = new PermissionResponseDto();
        expectedDto.setId(savedPermission.getId());
        expectedDto.setAction("CREATE");
        expectedDto.setResource("USER");

        when(permissionRepository.existsByActionAndResource("CREATE", "USER")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(savedPermission);
        when(permissionMapper.toDto(savedPermission)).thenReturn(expectedDto);

        // Act
        PermissionResponseDto result = permissionService.createPermission(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedPermission.getId());
        assertThat(result.getAction()).isEqualTo("CREATE");
        assertThat(result.getResource()).isEqualTo("USER");

        verify(permissionRepository).existsByActionAndResource("CREATE", "USER");
        verify(permissionRepository).save(any(Permission.class));
        verify(permissionMapper).toDto(savedPermission);
    }

    @Test
    void shouldThrowException_whenPermissionAlreadyExists() {
        // Arrange
        CreatePermissionRequest request = CreatePermissionRequest.builder()
                .action("CREATE")
                .resource("USER")
                .displayName("Create User")
                .build();

        when(permissionRepository.existsByActionAndResource("CREATE", "USER")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> permissionService.createPermission(request))
                .isInstanceOf(RuntimeException.class);

        verify(permissionRepository).existsByActionAndResource("CREATE", "USER");
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void shouldNormalizeActionAndResource_whenCreatingPermission() {
        // Arrange
        CreatePermissionRequest request = CreatePermissionRequest.builder()
                .action("create") // lowercase
                .resource("user") // lowercase
                .displayName("Create User")
                .build();

        Permission savedPermission = new Permission();
        savedPermission.setId(UUID.randomUUID());
        savedPermission.setAction("CREATE");
        savedPermission.setResource("USER");

        PermissionResponseDto expectedDto = new PermissionResponseDto();
        expectedDto.setId(savedPermission.getId());

        when(permissionRepository.existsByActionAndResource("CREATE", "USER")).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(savedPermission);
        when(permissionMapper.toDto(savedPermission)).thenReturn(expectedDto);

        // Act
        permissionService.createPermission(request);

        // Assert
        verify(permissionRepository).existsByActionAndResource("CREATE", "USER");
    }

    @Test
    void shouldUpdatePermission_whenValidRequest() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .displayName("Updated Display Name")
                .description("Updated description")
                .build();

        Permission existingPermission = new Permission();
        existingPermission.setId(permissionId);
        existingPermission.setAction("CREATE");
        existingPermission.setResource("USER");
        existingPermission.setDisplayName("Create User");

        Permission updatedPermission = new Permission();
        updatedPermission.setId(permissionId);
        updatedPermission.setAction("CREATE");
        updatedPermission.setResource("USER");
        updatedPermission.setDisplayName("Updated Display Name");

        PermissionResponseDto expectedDto = new PermissionResponseDto();
        expectedDto.setId(permissionId);
        expectedDto.setDisplayName("Updated Display Name");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(existingPermission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(updatedPermission);
        when(permissionMapper.toDto(updatedPermission)).thenReturn(expectedDto);

        // Act
        PermissionResponseDto result = permissionService.updatePermission(permissionId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDisplayName()).isEqualTo("Updated Display Name");

        verify(permissionRepository).findById(permissionId);
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistentPermission() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        UpdatePermissionRequest request = UpdatePermissionRequest.builder()
                .displayName("New Name")
                .build();

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> permissionService.updatePermission(permissionId, request))
                .isInstanceOf(RuntimeException.class);

        verify(permissionRepository).findById(permissionId);
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void shouldGetPermissionById_whenPermissionExists() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setAction("CREATE");
        permission.setResource("USER");

        PermissionResponseDto expectedDto = new PermissionResponseDto();
        expectedDto.setId(permissionId);
        expectedDto.setAction("CREATE");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        when(permissionMapper.toDto(permission)).thenReturn(expectedDto);

        // Act
        PermissionResponseDto result = permissionService.getPermissionById(permissionId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(permissionId);
        assertThat(result.getAction()).isEqualTo("CREATE");

        verify(permissionRepository).findById(permissionId);
        verify(permissionMapper).toDto(permission);
    }

    @Test
    void shouldThrowException_whenPermissionNotFound() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> permissionService.getPermissionById(permissionId))
                .isInstanceOf(RuntimeException.class);

        verify(permissionRepository).findById(permissionId);
        verify(permissionMapper, never()).toDto(any());
    }

    @Test
    void shouldDeletePermission_whenPermissionExists() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setAction("CREATE");
        permission.setResource("USER");

        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
        doNothing().when(permissionRepository).delete(permission);

        // Act
        permissionService.deletePermission(permissionId);

        // Assert
        verify(permissionRepository).findById(permissionId);
        verify(permissionRepository).delete(permission);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistentPermission() {
        // Arrange
        UUID permissionId = UUID.randomUUID();
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> permissionService.deletePermission(permissionId))
                .isInstanceOf(RuntimeException.class);

        verify(permissionRepository).findById(permissionId);
        verify(permissionRepository, never()).delete((Permission) any());
    }
}
