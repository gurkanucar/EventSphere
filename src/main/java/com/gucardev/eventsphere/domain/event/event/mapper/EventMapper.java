package com.gucardev.eventsphere.domain.event.event.mapper;

import com.gucardev.eventsphere.domain.event.event.entity.Event;
import com.gucardev.eventsphere.domain.event.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.event.model.request.CreateEventRequest;
import com.gucardev.eventsphere.domain.event.event.model.request.UpdateEventRequest;
import com.gucardev.eventsphere.domain.event.organizer.mapper.OrganizerMapper;
import com.gucardev.eventsphere.domain.event.session.mapper.SessionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrganizerMapper.class, SessionMapper.class})
public interface EventMapper {

    @Mapping(target = "organizer", source = "organizer")
    @Mapping(target = "sessions", source = "sessions")
    EventResponseDto toDto(Event event);

    Event toEntity(CreateEventRequest request);

    void updateEntityFromRequest(UpdateEventRequest request, @MappingTarget Event event);
}
