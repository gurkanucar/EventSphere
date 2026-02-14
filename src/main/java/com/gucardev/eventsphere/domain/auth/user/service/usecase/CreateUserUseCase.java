package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.model.request.CreateUserRequest;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements UseCase<CreateUserRequest, UserResponseDto> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto execute(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw ExceptionUtil.alreadyExists("User", "email", request.getEmail());
        }

        Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());

        if (roles.size() != request.getRoleIds().size()) {
            log.warn("Could not find all roles for IDs: {}", request.getRoleIds());
            throw ExceptionUtil.notFound("One or more roles", request.getRoleIds());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setActivated(request.getActivated() != null ? request.getActivated() : true);

        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("Successfully created user with email: {}", savedUser.getEmail());

        return userMapper.toUserResponseDto(savedUser);
    }
}