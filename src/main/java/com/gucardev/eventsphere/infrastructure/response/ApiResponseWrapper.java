package com.gucardev.eventsphere.infrastructure.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gucardev.eventsphere.infrastructure.config.message.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;

import java.util.Map;

@JsonPropertyOrder({
        "error",
        "traceId",
        "message",
        "data",
        "businessErrorCode",
        "validationErrors",
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper<T> {

    // Define the default message keys
    private static final String DEFAULT_SUCCESS_KEY = "success.default";
    private static final String DEFAULT_ERROR_KEY = "error.default";

    // Common fields
    private Boolean error;
    @Builder.Default
    private String traceId = MDC.get("traceId");
    private String message;

    // Success response field
    private T data;

    // Error response fields
    private Integer businessErrorCode;
    private Map<String, String> validationErrors;

    public static <T> ApiResponseWrapper<T> success(T data) {
        return success(data, DEFAULT_SUCCESS_KEY);
    }

    public static <T> ApiResponseWrapper<T> success(T data, String messageKey) {
        return ApiResponseWrapper.<T>builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(data)
                .build();
    }

    public static <U> ApiResponseWrapper<PageableResponse<U>> success(Page<U> page) {
        return success(page, DEFAULT_SUCCESS_KEY);
    }

    public static <U> ApiResponseWrapper<PageableResponse<U>> success(Page<U> page, String messageKey) {
        PageableResponse.PageDetails pageDetails = PageableResponse.PageDetails.builder()
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();

        PageableResponse<U> pageableResponse = PageableResponse.<U>builder()
                .content(page.getContent())
                .pageable(pageDetails)
                .build();

        return ApiResponseWrapper.<PageableResponse<U>>builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(pageableResponse)
                .build();
    }

    public static ApiResponseWrapper<Object> successWithEmptyData() {
        return successWithEmptyData(DEFAULT_SUCCESS_KEY);
    }

    public static ApiResponseWrapper<Object> successWithEmptyData(String messageKey) {
        return ApiResponseWrapper.builder()
                .error(false)
                .message(MessageUtil.getMessage(messageKey))
                .data(null)
                .build();
    }

    public static ApiResponseWrapper<Object> error(Integer businessErrorCode, Map<String, String> validationErrors) {
        return error(DEFAULT_ERROR_KEY, businessErrorCode, validationErrors);
    }

    public static ApiResponseWrapper<Object> error(String messageKey, Integer businessErrorCode,
                                                   Map<String, String> validationErrors) {
        return ApiResponseWrapper.builder()
                .error(true)
                .message(MessageUtil.getMessage(messageKey))
                .businessErrorCode(businessErrorCode)
                .validationErrors(validationErrors)
                .build();
    }

    public static ApiResponseWrapper<Object> error(Integer businessErrorCode, String customMessage,
                                                   Map<String, String> validationErrors) {
        return ApiResponseWrapper.builder()
                .error(true)
                .message(customMessage)
                .businessErrorCode(businessErrorCode)
                .validationErrors(validationErrors)
                .build();
    }

}