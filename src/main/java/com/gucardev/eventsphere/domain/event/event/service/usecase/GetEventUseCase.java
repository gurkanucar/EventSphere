package com.gucardev.eventsphere.domain.event.event.service.usecase;

import com.gucardev.eventsphere.domain.event.event.entity.Event;
import com.gucardev.eventsphere.domain.event.event.mapper.EventMapper;
import com.gucardev.eventsphere.domain.event.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.event.repository.EventRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetEventUseCase implements UseCase<UUID, EventResponseDto> {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto execute(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Event", id));
        return eventMapper.toDto(event);
    }
}
