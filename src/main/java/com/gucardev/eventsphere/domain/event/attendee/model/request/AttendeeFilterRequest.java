package com.gucardev.eventsphere.domain.event.attendee.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ParameterObject
public class AttendeeFilterRequest extends BaseFilterRequest {
    // Currently no specific filters for attendees other than pagination?
    // Maybe search by email/username through user join?
    // Keeping it simple for now. 
}
