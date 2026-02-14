package com.gucardev.eventsphere.domain.event.service.usecase;

import com.gucardev.eventsphere.domain.event.entity.Event;
import com.gucardev.eventsphere.domain.event.mapper.EventMapper;
import com.gucardev.eventsphere.domain.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.model.parameter.UpdateEventUseCaseParam;
import com.gucardev.eventsphere.domain.event.repository.EventRepository;
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
public class UpdateEventUseCase implements UseCase<UpdateEventUseCaseParam, EventResponseDto> {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public EventResponseDto execute(UpdateEventUseCaseParam param) {
        Event event = eventRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Event", param.id()));

        // Validate that the user updating this event is the owner of the organizer or admin
        ownershipValidator.validateOwnership(event.getOrganizer().getUser().getId());

        eventMapper.updateEntityFromRequest(param.request(), event);

        Event updatedEvent = eventRepository.save(event);
        log.info("Updated event: {}", updatedEvent.getId());

        return eventMapper.toDto(updatedEvent);
    }
}
