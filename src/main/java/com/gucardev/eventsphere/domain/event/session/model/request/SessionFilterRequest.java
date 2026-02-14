package com.gucardev.eventsphere.domain.event.session.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

import java.util.UUID;

@Getter
@Setter
@ParameterObject
public class SessionFilterRequest extends BaseFilterRequest {
    private String title;
    private String speakerName;
    private UUID eventId;
}
