package com.gucardev.eventsphere.domain.attendee.mapper;

import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.model.request.CreateAttendeeRequest;
import com.gucardev.eventsphere.domain.attendee.model.request.UpdateAttendeeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttendeeMapper {

    @Mapping(target = "userId", source = "user.id")
    AttendeeResponseDto toDto(Attendee attendee);

    Attendee toEntity(CreateAttendeeRequest request);

    void updateEntityFromRequest(UpdateAttendeeRequest request, @MappingTarget Attendee attendee);
}
