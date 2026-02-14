package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOptionalUserDtoByEmailUseCase implements UseCase<String, Optional<UserResponseDto>> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<UserResponseDto> execute(String input) {
        return userRepository.findByEmail(input).map(userMapper::toUserResponseDto);
    }
}
