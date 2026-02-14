package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase implements UseCase<UUID, UserResponseDto> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto execute(UUID userId) {
        log.debug("Fetching user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtil.notFound("User", userId));

        log.info("Successfully retrieved user: {}", user.getEmail());

        return userMapper.toUserResponseDto(user);
    }
}
