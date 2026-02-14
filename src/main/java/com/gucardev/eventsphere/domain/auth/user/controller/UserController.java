package com.gucardev.eventsphere.domain.auth.user.controller;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.model.parameter.UpdateUserUseCaseParam;
import com.gucardev.eventsphere.domain.auth.user.model.request.CreateUserRequest;
import com.gucardev.eventsphere.domain.auth.user.model.request.UpdateUserRequest;
import com.gucardev.eventsphere.domain.auth.user.model.request.UserFilterRequest;
import com.gucardev.eventsphere.domain.auth.user.service.usecase.*;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import com.gucardev.eventsphere.infrastructure.response.PageableResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users") // Added /v1 for versioning, like in the example
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for creating, reading, updating, and deleting users.")
public class UserController {

        // --- Use Cases ---
        private final CreateUserUseCase createUserUseCase;
        private final UpdateUserUseCase updateUserUseCase;
        private final DeleteUserUseCase deleteUserUseCase;
        private final GetUserByIdUseCase getUserByIdUseCase;
        private final GetAllUsersUseCase getAllUsersUseCase;

        // --- Controller Endpoints ---

        @Operation(summary = "Create a new user", description = "Creates a new user account.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PostMapping
        @PreAuthorize("hasAuthority('USER:CREATE')")
        public ResponseEntity<ApiResponseWrapper<UserResponseDto>> createUser(
                        @Valid @RequestBody CreateUserRequest request) {
                UserResponseDto createdUser = createUserUseCase.execute(request);
                return new ResponseEntity<>(ApiResponseWrapper.success(createdUser), HttpStatus.CREATED);
        }

        @Operation(summary = "Update an existing user", description = "Updates the details of an existing user by their ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('USER:UPDATE')")
        public ResponseEntity<ApiResponseWrapper<UserResponseDto>> updateUser(
                        @Parameter(description = "ID of the user to update.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
                        @Valid @RequestBody UpdateUserRequest request) {

                // Assuming you create this param object, following the example's pattern
                UpdateUserUseCaseParam param = new UpdateUserUseCaseParam(id, request);
                UserResponseDto updatedUser = updateUserUseCase.execute(param);
                return ResponseEntity.ok(ApiResponseWrapper.success(updatedUser));
        }

        @Operation(summary = "Delete a user", description = "Deletes a user by their ID (e.g., soft delete).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User deleted successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('USER:DELETE')")
        public ResponseEntity<ApiResponseWrapper<Object>> deleteUser(
                        @Parameter(description = "ID of the user to delete.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
                deleteUserUseCase.execute(id);
                return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
        }

        @Operation(summary = "Get a user by ID", description = "Retrieves the details of a specific user by their ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User found.", content = @Content(schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('USER:READ')")
        public ResponseEntity<ApiResponseWrapper<UserResponseDto>> getUserById(
                        @Parameter(description = "ID of the user to retrieve.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
                UserResponseDto user = getUserByIdUseCase.execute(id);
                return ResponseEntity.ok(ApiResponseWrapper.success(user));
        }

        @Operation(summary = "Get all users with filtering and pagination", description = "Retrieves a paginated list of users based on filter criteria.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users.", content = @Content(schema = @Schema(implementation = UserPageResponse.class)))
        })
        @GetMapping
        @PreAuthorize("hasAuthority('USER:READ')")
        public ResponseEntity<ApiResponseWrapper<PageableResponse<UserResponseDto>>> getAllUsers(
                        @ParameterObject @Valid UserFilterRequest filter) {
                Page<UserResponseDto> usersPage = getAllUsersUseCase.execute(filter);
                // The ApiResponseWrapper is assumed to handle Page -> PageableResponse
                // conversion
                return ResponseEntity.ok(ApiResponseWrapper.success(usersPage));
        }

        // --- OpenAPI Schema Helper Classes ---

        @Schema(name = "UserResponse", description = "API response containing a single user object.")
        public static class UserResponse extends ApiResponseWrapper<UserResponseDto> {
        }

        @Schema(name = "UserListResponse", description = "API response containing a list of user objects.")
        public static class UserListResponse extends ApiResponseWrapper<List<UserResponseDto>> {
        }

        @Schema(name = "UserPageResponse", description = "API response containing a paginated list of user objects.")
        public static class UserPageResponse extends ApiResponseWrapper<PageableResponse<UserResponseDto>> {
        }
}