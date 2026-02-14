package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.model.parameter.UpdateUserUseCaseParam;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserUseCase implements UseCase<UpdateUserUseCaseParam, UserResponseDto> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto execute(UpdateUserUseCaseParam param) {
        // Guard clause: validate input
        if (param == null || param.id() == null) {
            throw ExceptionUtil.of(com.gucardev.eventsphere.infrastructure.exception.ExceptionType.VALIDATION_FAILED);
        }

        // Fetch existing user
        User existingUser = userRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("User", param.id()));

        // Update fields if provided
        updateUserFields(existingUser, param);

        // Update roles if provided
        if (param.request().getRoleIds() != null && !param.request().getRoleIds().isEmpty()) {
            updateUserRoles(existingUser, param.request().getRoleIds());
        }

        // Save and return
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}", updatedUser.getId());

        return userMapper.toUserResponseDto(updatedUser);
    }

    private void updateUserFields(User user, UpdateUserUseCaseParam param) {
        var request = param.request();

        if (StringUtils.hasText(request.getEmail())) {
            // Check if email is being changed and if new email already exists
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw ExceptionUtil.alreadyExists("User", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        if (StringUtils.hasText(request.getSurname())) {
            user.setSurname(request.getSurname());
        }

        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getActivated() != null) {
            user.setActivated(request.getActivated());
        }
    }

    private void updateUserRoles(User user, Set<java.util.UUID> roleIds) {
        Set<Role> roles = roleRepository.findByIdIn(roleIds);

        if (roles.size() != roleIds.size()) {
            log.warn("Could not find all roles for IDs: {}", roleIds);
            throw ExceptionUtil.notFound("One or more roles", roleIds);
        }

        user.setRoles(roles);
    }
}
