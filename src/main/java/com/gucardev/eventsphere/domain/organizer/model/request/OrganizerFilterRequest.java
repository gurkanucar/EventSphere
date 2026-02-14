package com.gucardev.eventsphere.domain.organizer.model.request;

import com.gucardev.eventsphere.domain.shared.model.request.BaseFilterRequest;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ParameterObject
public class OrganizerFilterRequest extends BaseFilterRequest {
    private String organizationName;
    private String contactEmail;
}
