package com.gucardev.eventsphere.domain.event.session.service.usecase;

import com.gucardev.eventsphere.domain.event.event.entity.Event;
import com.gucardev.eventsphere.domain.event.event.repository.EventRepository;
import com.gucardev.eventsphere.domain.event.session.entity.Session;
import com.gucardev.eventsphere.domain.event.session.mapper.SessionMapper;
import com.gucardev.eventsphere.domain.event.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.event.session.model.request.CreateSessionRequest;
import com.gucardev.eventsphere.domain.event.session.repository.SessionRepository;
import com.gucardev.eventsphere.domain.shared.util.ResourceOwnershipValidator;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateSessionUseCase implements UseCase<CreateSessionRequest, SessionResponseDto> {

    private final SessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final SessionMapper sessionMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public SessionResponseDto execute(CreateSessionRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> ExceptionUtil.notFound("Event", request.eventId()));

        // Check if user is organizer of the event
        ownershipValidator.validateOwnership(event.getOrganizer().getUser().getId());

        Session session = sessionMapper.toEntity(request);
        session.setEvent(event);
        event.addSession(session);

        Session savedSession = sessionRepository.save(session);
        log.info("Created session: {}", savedSession.getId());

        return sessionMapper.toDto(savedSession);
    }
}
