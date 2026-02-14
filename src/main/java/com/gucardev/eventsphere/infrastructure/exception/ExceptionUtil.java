package com.gucardev.eventsphere.infrastructure.exception;

import com.gucardev.eventsphere.infrastructure.config.message.MessageUtil;

public final class ExceptionUtil {

    private ExceptionUtil() {}

    // ==================== Generic ====================

    public static BusinessException of(ExceptionType type) {
        return new BusinessException(
                MessageUtil.getMessage(type.getKey()),
                type.getStatus(),
                type.getCode()
        );
    }

    public static BusinessException of(ExceptionType type, Object... args) {
        return new BusinessException(
                MessageUtil.getMessage(type.getKey(), args),
                type.getStatus(),
                type.getCode()
        );
    }

    // ==================== Convenience Methods ====================

    /**
     * Generic not found - pass entity name and id
     * Usage: notFound("Product", 123)
     */
    public static BusinessException notFound(String entity, Object id) {
        return of(ExceptionType.NOT_FOUND, entity, id);
    }

    /**
     * Generic already exists
     * Usage: alreadyExists("User", "email", "test@example.com")
     */
    public static BusinessException alreadyExists(String entity, String field, Object value) {
        return of(ExceptionType.ALREADY_EXISTS, entity, field, value);
    }

    /**
     * Simple already exists
     * Usage: alreadyExists("Product SKU")
     */
    public static BusinessException alreadyExists(String what) {
        return of(ExceptionType.ALREADY_EXISTS, what);
    }

    public static BusinessException forbidden() {
        return of(ExceptionType.FORBIDDEN);
    }

    public static BusinessException unauthorized() {
        return of(ExceptionType.UNAUTHORIZED);
    }
}