package com.gucardev.eventsphere.domain.event.organizer.mapper;

import com.gucardev.eventsphere.domain.event.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.event.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.event.organizer.model.request.CreateOrganizerRequest;
import com.gucardev.eventsphere.domain.event.organizer.model.request.UpdateOrganizerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrganizerMapper {

    @Mapping(target = "userId", source = "user.id")
    OrganizerResponseDto toDto(Organizer organizer);

    Organizer toEntity(CreateOrganizerRequest request);

    void updateEntityFromRequest(UpdateOrganizerRequest request, @MappingTarget Organizer organizer);
}
