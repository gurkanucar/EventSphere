package com.gucardev.eventsphere.domain.event.controller;

import com.gucardev.eventsphere.domain.event.model.dto.EventResponseDto;
import com.gucardev.eventsphere.domain.event.model.parameter.UpdateEventUseCaseParam;
import com.gucardev.eventsphere.domain.event.model.request.CreateEventRequest;
import com.gucardev.eventsphere.domain.event.model.request.EventFilterRequest;
import com.gucardev.eventsphere.domain.event.model.request.UpdateEventRequest;
import com.gucardev.eventsphere.domain.event.service.usecase.CreateEventUseCase;
import com.gucardev.eventsphere.domain.event.service.usecase.GetEventListUseCase;
import com.gucardev.eventsphere.domain.event.service.usecase.GetEventUseCase;
import com.gucardev.eventsphere.domain.event.service.usecase.UpdateEventUseCase;
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
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "Event Management APIs")
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final UpdateEventUseCase updateEventUseCase;
    private final GetEventUseCase getEventUseCase;
    private final GetEventListUseCase getEventListUseCase;

    @PostMapping
    @Operation(summary = "Create a new event", description = "Creates a new event for a specific organizer.")
    public ResponseEntity<ApiResponseWrapper<EventResponseDto>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success(createEventUseCase.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing event", description = "Updates details of an existing event.")
    public ResponseEntity<ApiResponseWrapper<EventResponseDto>> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(updateEventUseCase.execute(new UpdateEventUseCaseParam(id, request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves an event by its unique identifier.")
    public ResponseEntity<ApiResponseWrapper<EventResponseDto>> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getEventUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Search events", description = "Retrieves a paginated list of events based on filter criteria.")
    public ResponseEntity<ApiResponseWrapper<com.gucardev.eventsphere.infrastructure.response.PageableResponse<EventResponseDto>>> searchEvents(
            @Valid @ParameterObject EventFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getEventListUseCase.execute(filter)));
    }
}
