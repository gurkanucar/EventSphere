package com.gucardev.eventsphere.domain.event.ticket.controller;

import com.gucardev.eventsphere.domain.event.ticket.model.dto.TicketResponseDto;
import com.gucardev.eventsphere.domain.event.ticket.model.parameter.UpdateTicketUseCaseParam;
import com.gucardev.eventsphere.domain.event.ticket.model.request.CreateTicketRequest;
import com.gucardev.eventsphere.domain.event.ticket.model.request.TicketFilterRequest;
import com.gucardev.eventsphere.domain.event.ticket.model.request.UpdateTicketRequest;
import com.gucardev.eventsphere.domain.event.ticket.service.usecase.CreateTicketUseCase;
import com.gucardev.eventsphere.domain.event.ticket.service.usecase.GetTicketListUseCase;
import com.gucardev.eventsphere.domain.event.ticket.service.usecase.GetTicketUseCase;
import com.gucardev.eventsphere.domain.event.ticket.service.usecase.UpdateTicketUseCase;
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
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "Ticket Management APIs")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final UpdateTicketUseCase updateTicketUseCase;
    private final GetTicketUseCase getTicketUseCase;
    private final GetTicketListUseCase getTicketListUseCase;

    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Creates a new ticket for an event and attendee.")
    public ResponseEntity<ApiResponseWrapper<TicketResponseDto>> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success(createTicketUseCase.execute(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing ticket", description = "Updates details of an existing ticket.")
    public ResponseEntity<ApiResponseWrapper<TicketResponseDto>> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(updateTicketUseCase.execute(new UpdateTicketUseCaseParam(id, request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Retrieves a ticket by its unique identifier.")
    public ResponseEntity<ApiResponseWrapper<TicketResponseDto>> getTicket(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getTicketUseCase.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Search tickets", description = "Retrieves a paginated list of tickets based on filter criteria.")
    public ResponseEntity<ApiResponseWrapper<com.gucardev.eventsphere.infrastructure.response.PageableResponse<TicketResponseDto>>> searchTickets(
            @Valid @ParameterObject TicketFilterRequest filter) {
        return ResponseEntity.ok(ApiResponseWrapper.success(getTicketListUseCase.execute(filter)));
    }
}
