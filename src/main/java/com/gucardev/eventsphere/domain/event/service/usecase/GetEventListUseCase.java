package com.gucardev.eventsphere.domain.event.service.usecase;

import com.gucardev.eventsphere.domain.event.entity.Event;
import com.gucardev.eventsphere.domain.event.mapper.EventMapper;
import com.gucardev.eventsphere.domain.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.model.request.EventFilterRequest;
import com.gucardev.eventsphere.domain.event.repository.EventRepository;
import com.gucardev.eventsphere.domain.event.repository.specification.EventSpecification;
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
public class GetEventListUseCase implements UseCase<EventFilterRequest, Page<EventResponseDto>> {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDto> execute(EventFilterRequest filter) {
        Specification<Event> spec = BaseSpecification.toSpec(filter);

        if (StringUtils.hasText(filter.getTitle())) {
            spec = spec.and(EventSpecification.withTitle(filter.getTitle()));
        }
        if (StringUtils.hasText(filter.getLocation())) {
            spec = spec.and(EventSpecification.withLocation(filter.getLocation()));
        }
        if (filter.getIsPublished() != null) {
            spec = spec.and(EventSpecification.isPublished(filter.getIsPublished()));
        }

        // Fetch organizer to avoid N+1
        spec = spec.and(EventSpecification.fetchOrganizer());

        Pageable pageable = filter.toPageable();
        return eventRepository.findAll(spec, pageable).map(eventMapper::toDto);
    }
}
