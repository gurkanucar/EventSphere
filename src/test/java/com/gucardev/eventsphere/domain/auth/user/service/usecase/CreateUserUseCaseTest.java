package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.model.request.CreateUserRequest;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private RoleRepository roleRepository;

        @Mock
        private UserMapper userMapper;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private org.springframework.context.MessageSource messageSource;

        @InjectMocks
        private CreateUserUseCase createUserUseCase;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
                new com.gucardev.eventsphere.infrastructure.config.message.MessageUtil(messageSource);
                lenient().when(messageSource.getMessage(anyString(), any(), any()))
                                .thenReturn("Test Message not found");
        }

        @Test
        void shouldCreateUser_whenValidRequest() {
                // Arrange
                UUID roleId = UUID.randomUUID();
                Set<UUID> roleIds = Set.of(roleId);

                CreateUserRequest request = CreateUserRequest.builder()
                                .email("john.doe@example.com")
                                .password("SecurePassword123")
                                .name("John")
                                .surname("Doe")
                                .phoneNumber("+1234567890")
                                .activated(true)
                                .roleIds(roleIds)
                                .build();

                Role role = new Role();
                role.setId(roleId);
                role.setName("USER");

                User savedUser = new User();
                savedUser.setId(UUID.randomUUID());
                savedUser.setEmail("john.doe@example.com");
                savedUser.setName("John");
                savedUser.setSurname("Doe");
                savedUser.setActivated(true);

                UserResponseDto expectedDto = new UserResponseDto();
                expectedDto.setId(savedUser.getId());
                expectedDto.setEmail("john.doe@example.com");

                when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
                when(roleRepository.findByIdIn(roleIds)).thenReturn(Set.of(role));
                when(passwordEncoder.encode("SecurePassword123")).thenReturn("encodedPassword");
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedDto);

                // Act
                UserResponseDto result = createUserUseCase.execute(request);

                // Assert
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(savedUser.getId());
                assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

                verify(userRepository).findByEmail("john.doe@example.com");
                verify(roleRepository).findByIdIn(roleIds);
                verify(passwordEncoder).encode("SecurePassword123");
                verify(userRepository).save(any(User.class));
                verify(userMapper).toUserResponseDto(savedUser);
        }

        @Test
        void shouldThrowException_whenEmailAlreadyExists() {
                // Arrange
                CreateUserRequest request = CreateUserRequest.builder()
                                .email("existing@example.com")
                                .password("password")
                                .name("John")
                                .surname("Doe")
                                .roleIds(Set.of(UUID.randomUUID()))
                                .build();

                User existingUser = new User();
                existingUser.setEmail("existing@example.com");

                when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

                // Act & Assert
                assertThatThrownBy(() -> createUserUseCase.execute(request))
                                .isInstanceOf(com.gucardev.eventsphere.infrastructure.exception.BusinessException.class);

                verify(userRepository).findByEmail("existing@example.com");
                verify(userRepository, never()).save(any());
        }

        @Test
        void shouldThrowException_whenRoleNotFound() {
                // Arrange
                UUID roleId1 = UUID.randomUUID();
                UUID roleId2 = UUID.randomUUID();
                Set<UUID> roleIds = Set.of(roleId1, roleId2);

                CreateUserRequest request = CreateUserRequest.builder()
                                .email("john.doe@example.com")
                                .password("password")
                                .name("John")
                                .surname("Doe")
                                .roleIds(roleIds)
                                .build();

                Role role = new Role();
                role.setId(roleId1);

                when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
                when(roleRepository.findByIdIn(roleIds)).thenReturn(Set.of(role)); // Only one role found

                // Act & Assert
                assertThatThrownBy(() -> createUserUseCase.execute(request))
                                .isInstanceOf(com.gucardev.eventsphere.infrastructure.exception.BusinessException.class)
                                .hasMessageContaining("not found");

                verify(userRepository).findByEmail("john.doe@example.com");
                verify(roleRepository).findByIdIn(roleIds);
                verify(userRepository, never()).save(any());
        }

        @Test
        void shouldSetActivatedToTrue_whenNotProvided() {
                // Arrange
                UUID roleId = UUID.randomUUID();
                Set<UUID> roleIds = Set.of(roleId);

                CreateUserRequest request = CreateUserRequest.builder()
                                .email("john.doe@example.com")
                                .password("password")
                                .name("John")
                                .surname("Doe")
                                .roleIds(roleIds)
                                .activated(null) // Not provided
                                .build();

                Role role = new Role();
                role.setId(roleId);

                User savedUser = new User();
                savedUser.setId(UUID.randomUUID());
                savedUser.setActivated(true);

                UserResponseDto expectedDto = new UserResponseDto();
                expectedDto.setId(savedUser.getId());

                when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
                when(roleRepository.findByIdIn(roleIds)).thenReturn(Set.of(role));
                when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedDto);

                // Act
                createUserUseCase.execute(request);

                // Assert
                verify(userRepository).save(argThat(user -> user.getActivated() == true));
        }

        @Test
        void shouldEncodePassword_whenCreatingUser() {
                // Arrange
                UUID roleId = UUID.randomUUID();
                String rawPassword = "MySecretPassword123";

                CreateUserRequest request = CreateUserRequest.builder()
                                .email("john.doe@example.com")
                                .password(rawPassword)
                                .name("John")
                                .surname("Doe")
                                .roleIds(Set.of(roleId))
                                .build();

                Role role = new Role();
                role.setId(roleId);

                User savedUser = new User();
                savedUser.setId(UUID.randomUUID());

                UserResponseDto expectedDto = new UserResponseDto();

                when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
                when(roleRepository.findByIdIn(any())).thenReturn(Set.of(role));
                when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword123");
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(userMapper.toUserResponseDto(any())).thenReturn(expectedDto);

                // Act
                createUserUseCase.execute(request);

                // Assert
                verify(passwordEncoder).encode(rawPassword);
                verify(userRepository).save(argThat(user -> user.getPassword().equals("encodedPassword123")));
        }
}
