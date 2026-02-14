package com.gucardev.eventsphere.infrastructure.exception;

import com.gucardev.eventsphere.infrastructure.config.message.MessageUtil;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleBusinessException(BusinessException ex) {
        log.warn("[HTTP] Business exception at {}: {}",
                ExceptionLogger.findOrigin(ex), ex.getMessage());
        return new ResponseEntity<>(
                ApiResponseWrapper.error(ex.getBusinessErrorCode(), ex.getMessage(), null),
                ex.getStatus()
        );
    }

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponseWrapper<Object>> handleAccessDenied(Exception ex) {
        log.warn("[HTTP] Access denied at {}: {}",
                ExceptionLogger.findOrigin(ex), ex.getMessage());
        ApiResponseWrapper<Object> response = new ApiResponseWrapper<>();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class,
            AccountStatusException.class
    })
    public ResponseEntity<ApiResponseWrapper<Object>> handleAuthenticationException(Exception ex) {
        log.warn("[HTTP] Authentication failed: {}", ex.getMessage());

        String message = "Invalid email or password";

        // If the account is strictly locked/disabled, you might want to show that specific message
        if (ex instanceof AccountStatusException) {
            message = ex.getMessage();
        }

        return new ResponseEntity<>(
                ApiResponseWrapper.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        message,
                        null
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ApiResponseWrapper<Object>> handleValidation(Exception ex) {
        Map<String, String> errors = extractValidationErrors(ex);
        log.warn("[HTTP] Validation failed: {}", errors);
        return new ResponseEntity<>(
                ApiResponseWrapper.error("validation.failed",
                        ExceptionType.DEFAULT.getCode(), errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("[HTTP] Invalid request body: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponseWrapper.error(
                        "invalid.request.body",
                        ExceptionType.DEFAULT.getCode(),
                        null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleNoResource(NoResourceFoundException ex) {
        return new ResponseEntity<>(
                ApiResponseWrapper.error(404, MessageUtil.getMessage("error.resource.not.found"), null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleAll(Exception ex) {
        String message;
        if (isDatabaseException(ex)) {
            ExceptionLogger.logError(log, "HTTP-DB", ex);
            message = MessageUtil.getMessage("database.error");
        } else {
            ExceptionLogger.logError(log, "HTTP", ex);
            message = "An unexpected error occurred";
        }
        return new ResponseEntity<>(
                ApiResponseWrapper.error(ExceptionType.DEFAULT.getCode(), message, null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private Map<String, String> extractValidationErrors(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException mex) {
            mex.getBindingResult().getAllErrors().forEach(err -> {
                String field = err instanceof FieldError fe ? fe.getField() : err.getObjectName();
                errors.put(field, err.getDefaultMessage());
            });
        } else if (ex instanceof ConstraintViolationException cex) {
            cex.getConstraintViolations().forEach(v ->
                    errors.put(v.getPropertyPath().toString(), v.getMessage()));
        } else if (ex instanceof HandlerMethodValidationException hmvex) {
            hmvex.getAllErrors().forEach(err -> {
                String field;
                if (err instanceof FieldError fe) {
                    field = fe.getField();
                } else {
                    // Extract parameter name from codes like "Email.exceptionTestController#testParamValidation.email"
                    field = "parameter";
                    if (err.getCodes() != null && err.getCodes().length > 0) {
                        String code = err.getCodes()[0];
                        int lastDotIndex = code.lastIndexOf('.');
                        if (lastDotIndex > 0 && lastDotIndex < code.length() - 1) {
                            field = code.substring(lastDotIndex + 1);
                        }
                    }
                }
                errors.put(field, err.getDefaultMessage());
            });
        }
        return errors;
    }

    private boolean isDatabaseException(Throwable ex) {
        for (Throwable c = ex; c != null; c = c.getCause()) {
            if (c instanceof SQLException || c instanceof DataAccessException || c instanceof PersistenceException)
                return true;
        }
        return false;
    }
}