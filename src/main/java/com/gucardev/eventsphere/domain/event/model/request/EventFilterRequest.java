package com.gucardev.eventsphere.domain.event.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ParameterObject
public class EventFilterRequest extends BaseFilterRequest {
    private String title;
    private String location;
    private Boolean isPublished;
}
