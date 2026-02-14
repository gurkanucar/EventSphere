package com.gucardev.eventsphere.domain.event.session.mapper;

import com.gucardev.eventsphere.domain.event.session.entity.Session;
import com.gucardev.eventsphere.domain.event.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.event.session.model.request.CreateSessionRequest;
import com.gucardev.eventsphere.domain.event.session.model.request.UpdateSessionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    @Mapping(target = "eventId", source = "event.id")
    SessionResponseDto toDto(Session session);

    Session toEntity(CreateSessionRequest request);

    void updateEntityFromRequest(UpdateSessionRequest request, @MappingTarget Session session);
}
