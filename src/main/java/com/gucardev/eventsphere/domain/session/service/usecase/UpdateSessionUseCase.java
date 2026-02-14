package com.gucardev.eventsphere.domain.session.service.usecase;

import com.gucardev.eventsphere.domain.session.entity.Session;
import com.gucardev.eventsphere.domain.session.mapper.SessionMapper;
import com.gucardev.eventsphere.domain.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.session.model.parameter.UpdateSessionUseCaseParam;
import com.gucardev.eventsphere.domain.session.repository.SessionRepository;
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
public class UpdateSessionUseCase implements UseCase<UpdateSessionUseCaseParam, SessionResponseDto> {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public SessionResponseDto execute(UpdateSessionUseCaseParam param) {
        Session session = sessionRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Session", param.id()));

        // Validate that the user updating this session is the owner of the event organizer
        ownershipValidator.validateOwnership(session.getEvent().getOrganizer().getUser().getId());

        sessionMapper.updateEntityFromRequest(param.request(), session);

        Session updatedSession = sessionRepository.save(session);
        log.info("Updated session: {}", updatedSession.getId());

        return sessionMapper.toDto(updatedSession);
    }
}
