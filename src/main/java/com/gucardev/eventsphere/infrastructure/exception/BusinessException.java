package com.gucardev.eventsphere.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final Integer businessErrorCode;

    public BusinessException(String message, HttpStatus status, Integer businessErrorCode) {
        super(message);
        this.status = status;
        this.businessErrorCode = businessErrorCode;
    }

}