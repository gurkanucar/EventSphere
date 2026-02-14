package com.gucardev.eventsphere.infrastructure.config.security.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    private String email;
    private String name;
    private String surname;

}
