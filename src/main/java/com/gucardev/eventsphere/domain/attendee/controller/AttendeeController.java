package com.gucardev.eventsphere.domain.attendee.controller;

import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.model.parameter.UpdateAttendeeUseCaseParam;
import com.gucardev.eventsphere.domain.attendee.model.request.AttendeeFilterRequest;
import com.gucardev.eventsphere.domain.attendee.model.request.CreateAttendeeRequest;
import com.gucardev.eventsphere.domain.attendee.model.request.UpdateAttendeeRequest;
import com.gucardev.eventsphere.domain.attendee.service.usecase.CreateAttendeeUseCase;
import com.gucardev.eventsphere.domain.attendee.service.usecase.GetAttendeeListUseCase;
import com.gucardev.eventsphere.domain.attendee.service.usecase.GetAttendeeUseCase;
import com.gucardev.eventsphere.domain.attendee.service.usecase.UpdateAttendeeUseCase;
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
@RequestMapping("/api/v1/attendees")
@RequiredArgsConstructor
@Tag(name = "Attendee", description = "Attendee Management APIs")
public class AttendeeController {

    private final CreateAttendeeUseCase createAttendeeUseCase;
    private final UpdateAttendeeUseCase updateAttendeeUseCase;
    private final GetAttendeeUseCase getAttendeeUseCase;
    private final GetAttendeeListUseCase getAttendeeListUseCase;

    @PostMapping
    @Operation(summary = "Create a new attendee", description = "Creates a new attendee profile for a user.")
    public ResponseEntity<ApiResponseWrapper<AttendeeResponseDto>> createAttendee(@Valid @RequestBody CreateAttendeeRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success(createAttendeeUseCase.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing attendee", description = "Updates details of an existing attendee.")
    public ResponseEntity<ApiResponseWrapper<AttendeeResponseDto>> updateAttendee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAttendeeRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(updateAttendeeUseCase.execute(new UpdateAttendeeUseCaseParam(id, request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attendee by ID", description = "Retrieves an attendee by its unique identifier.")
    public ResponseEntity<ApiResponseWrapper<AttendeeResponseDto>> getAttendee(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getAttendeeUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Search attendees", description = "Retrieves a paginated list of attendees based on filter criteria.")
    public ResponseEntity<ApiResponseWrapper<com.gucardev.eventsphere.infrastructure.response.PageableResponse<AttendeeResponseDto>>> searchAttendees(
            @Valid @ParameterObject AttendeeFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getAttendeeListUseCase.execute(filter)));
    }
}
