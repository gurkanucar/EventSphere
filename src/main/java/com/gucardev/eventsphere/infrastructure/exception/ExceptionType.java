package com.gucardev.eventsphere.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    // ==================== COMMON ====================
    DEFAULT("error.default", HttpStatus.BAD_REQUEST, 1000),
    NOT_FOUND("error.not_found", HttpStatus.NOT_FOUND, 1001),
    ALREADY_EXISTS("error.already_exists", HttpStatus.CONFLICT, 1002),
    VALIDATION_FAILED("error.validation_failed", HttpStatus.BAD_REQUEST, 1003),

    // ==================== AUTH ====================
    UNAUTHORIZED("error.auth.unauthorized", HttpStatus.UNAUTHORIZED, 1100),
    FORBIDDEN("error.auth.forbidden", HttpStatus.FORBIDDEN, 1101),
    TOKEN_EXPIRED("error.auth.token_expired", HttpStatus.UNAUTHORIZED, 1102),
    INVALID_CREDENTIALS("error.auth.invalid_credentials", HttpStatus.UNAUTHORIZED, 1103),

    // ==================== BUSINESS SPECIFIC ====================
    OUT_OF_STOCK("error.product.out_of_stock", HttpStatus.CONFLICT, 1201),
    ORDER_ALREADY_CANCELLED("error.order.already_cancelled", HttpStatus.CONFLICT, 1301),
    PAYMENT_FAILED("error.order.payment_failed", HttpStatus.PAYMENT_REQUIRED, 1302),
    USER_INACTIVE("error.user.inactive", HttpStatus.FORBIDDEN, 1402);

    private final String key;
    private final HttpStatus status;
    private final int code;
}