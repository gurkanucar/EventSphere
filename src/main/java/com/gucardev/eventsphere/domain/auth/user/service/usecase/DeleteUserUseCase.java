package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCaseWithInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserUseCase implements UseCaseWithInput<UUID> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(UUID userId) {
        log.debug("Attempting to delete user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtil.notFound("User", userId));

        userRepository.delete(user);

        log.info("Successfully deleted user with ID: {} (email: {})", userId, user.getEmail());
    }
}
