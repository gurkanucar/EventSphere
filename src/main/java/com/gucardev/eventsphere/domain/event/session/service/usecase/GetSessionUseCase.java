package com.gucardev.eventsphere.domain.event.session.service.usecase;

import com.gucardev.eventsphere.domain.event.session.entity.Session;
import com.gucardev.eventsphere.domain.event.session.mapper.SessionMapper;
import com.gucardev.eventsphere.domain.event.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.event.session.repository.SessionRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSessionUseCase implements UseCase<UUID, SessionResponseDto> {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto execute(UUID id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Session", id));
        return sessionMapper.toDto(session);
    }
}
