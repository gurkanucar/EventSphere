package com.gucardev.eventsphere.domain.session.service.usecase;

import com.gucardev.eventsphere.domain.session.entity.Session;
import com.gucardev.eventsphere.domain.session.mapper.SessionMapper;
import com.gucardev.eventsphere.domain.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.session.model.request.SessionFilterRequest;
import com.gucardev.eventsphere.domain.session.repository.specification.SessionSpecification;
import com.gucardev.eventsphere.domain.session.repository.SessionRepository;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GetSessionListUseCase implements UseCase<SessionFilterRequest, Page<SessionResponseDto>> {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponseDto> execute(SessionFilterRequest filter) {
        Specification<Session> spec = BaseSpecification.toSpec(filter);

        if (StringUtils.hasText(filter.getTitle())) {
            spec = spec.and(SessionSpecification.withTitle(filter.getTitle()));
        }
        if (StringUtils.hasText(filter.getSpeakerName())) {
            spec = spec.and(SessionSpecification.withSpeakerName(filter.getSpeakerName()));
        }
        if (filter.getEventId() != null) {
            spec = spec.and(SessionSpecification.withEventId(filter.getEventId()));
        }

        // Fetch event to avoid N+1
        spec = spec.and(SessionSpecification.fetchEvent());

        Pageable pageable = filter.toPageable();
        return sessionRepository.findAll(spec, pageable).map(sessionMapper::toDto);
    }
}
