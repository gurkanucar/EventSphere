package com.gucardev.eventsphere.domain.session.controller;

import com.gucardev.eventsphere.domain.session.model.dto.SessionResponseDto;
import com.gucardev.eventsphere.domain.session.model.parameter.UpdateSessionUseCaseParam;
import com.gucardev.eventsphere.domain.session.model.request.CreateSessionRequest;
import com.gucardev.eventsphere.domain.session.model.request.SessionFilterRequest;
import com.gucardev.eventsphere.domain.session.model.request.UpdateSessionRequest;
import com.gucardev.eventsphere.domain.session.service.usecase.CreateSessionUseCase;
import com.gucardev.eventsphere.domain.session.service.usecase.GetSessionListUseCase;
import com.gucardev.eventsphere.domain.session.service.usecase.GetSessionUseCase;
import com.gucardev.eventsphere.domain.session.service.usecase.UpdateSessionUseCase;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Session", description = "Session Management APIs")
public class SessionController {

    private final CreateSessionUseCase createSessionUseCase;
    private final UpdateSessionUseCase updateSessionUseCase;
    private final GetSessionUseCase getSessionUseCase;
    private final GetSessionListUseCase getSessionListUseCase;

    @PostMapping
    @Operation(summary = "Create a new session", description = "Creates a new session for an event.")
    public ResponseEntity<ApiResponseWrapper<SessionResponseDto>> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success(createSessionUseCase.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing session", description = "Updates details of an existing session.")
    public ResponseEntity<ApiResponseWrapper<SessionResponseDto>> updateSession(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSessionRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(updateSessionUseCase.execute(new UpdateSessionUseCaseParam(id, request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session by ID", description = "Retrieves a session by its unique identifier.")
    public ResponseEntity<ApiResponseWrapper<SessionResponseDto>> getSession(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getSessionUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Search sessions", description = "Retrieves a paginated list of sessions based on filter criteria.")
    public ResponseEntity<ApiResponseWrapper<com.gucardev.eventsphere.infrastructure.response.PageableResponse<SessionResponseDto>>> searchSessions(
            @Valid @ParameterObject SessionFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getSessionListUseCase.execute(filter)));
    }
}
