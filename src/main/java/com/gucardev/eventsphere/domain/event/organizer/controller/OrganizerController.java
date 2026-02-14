package com.gucardev.eventsphere.domain.event.organizer.controller;

import com.gucardev.eventsphere.domain.event.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.event.organizer.model.parameter.UpdateOrganizerUseCaseParam;
import com.gucardev.eventsphere.domain.event.organizer.model.request.CreateOrganizerRequest;
import com.gucardev.eventsphere.domain.event.organizer.model.request.OrganizerFilterRequest;
import com.gucardev.eventsphere.domain.event.organizer.model.request.UpdateOrganizerRequest;
import com.gucardev.eventsphere.domain.event.organizer.service.usecase.CreateOrganizerUseCase;
import com.gucardev.eventsphere.domain.event.organizer.service.usecase.GetOrganizerListUseCase;
import com.gucardev.eventsphere.domain.event.organizer.service.usecase.GetOrganizerUseCase;
import com.gucardev.eventsphere.domain.event.organizer.service.usecase.UpdateOrganizerUseCase;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizers")
@RequiredArgsConstructor
@Tag(name = "Organizer", description = "Organizer Management APIs")
public class OrganizerController {

    private final CreateOrganizerUseCase createOrganizerUseCase;
    private final UpdateOrganizerUseCase updateOrganizerUseCase;
    private final GetOrganizerUseCase getOrganizerUseCase;
    private final GetOrganizerListUseCase getOrganizerListUseCase;

    @PostMapping
    @Operation(summary = "Create a new organizer", description = "Creates a new organizer profile for a user.")
    public ResponseEntity<ApiResponseWrapper<OrganizerResponseDto>> createOrganizer(@Valid @RequestBody CreateOrganizerRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success(createOrganizerUseCase.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing organizer", description = "Updates details of an existing organizer.")
    public ResponseEntity<ApiResponseWrapper<OrganizerResponseDto>> updateOrganizer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizerRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(updateOrganizerUseCase.execute(new UpdateOrganizerUseCaseParam(id, request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organizer by ID", description = "Retrieves an organizer by its unique identifier.")
    public ResponseEntity<ApiResponseWrapper<OrganizerResponseDto>> getOrganizer(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getOrganizerUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Search organizers", description = "Retrieves a paginated list of organizers based on filter criteria.")
    public ResponseEntity<ApiResponseWrapper<com.gucardev.eventsphere.infrastructure.response.PageableResponse<OrganizerResponseDto>>> searchOrganizers(
            @Valid @ParameterObject OrganizerFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getOrganizerListUseCase.execute(filter)));
    }
}
