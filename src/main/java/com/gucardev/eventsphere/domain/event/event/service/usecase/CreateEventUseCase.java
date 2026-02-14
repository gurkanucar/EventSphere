package com.gucardev.eventsphere.domain.event.event.service.usecase;

import com.gucardev.eventsphere.domain.event.event.entity.Event;
import com.gucardev.eventsphere.domain.event.event.mapper.EventMapper;
import com.gucardev.eventsphere.domain.event.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.event.model.request.CreateEventRequest;
import com.gucardev.eventsphere.domain.event.event.repository.EventRepository;
import com.gucardev.eventsphere.domain.event.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.event.organizer.repository.OrganizerRepository;
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
public class CreateEventUseCase implements UseCase<CreateEventRequest, EventResponseDto> {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final EventMapper eventMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public EventResponseDto execute(CreateEventRequest request) {
        Organizer organizer = organizerRepository.findById(request.organizerId())
                .orElseThrow(() -> ExceptionUtil.notFound("Organizer", request.organizerId()));

        // Validate that the user creating this event is the owner of the organizer or admin
        ownershipValidator.validateOwnership(organizer.getUser().getId());

        Event event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);
        organizer.addEvent(event);

        Event savedEvent = eventRepository.save(event);
        log.info("Created event: {}", savedEvent.getId());

        return eventMapper.toDto(savedEvent);
    }
}
