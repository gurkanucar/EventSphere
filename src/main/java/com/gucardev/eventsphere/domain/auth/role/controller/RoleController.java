package com.gucardev.eventsphere.domain.auth.role.controller;

import com.gucardev.eventsphere.domain.auth.role.model.dto.RoleResponseDto;
import com.gucardev.eventsphere.domain.auth.role.model.request.CreateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.RoleFilterRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.UpdateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.service.RoleService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for creating, reading, updating, and deleting roles and their permissions.")
public class RoleController {

        private final RoleService roleService;

        @Operation(summary = "Create a new role", description = "Creates a new role with optional permissions.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Role created successfully.", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "409", description = "Role with this name already exists.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PostMapping
        @PreAuthorize("hasAuthority('ROLE:CREATE')")
        public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> createRole(
                        @Valid @RequestBody CreateRoleRequest request) {
                RoleResponseDto createdRole = roleService.createRole(request);
                return new ResponseEntity<>(ApiResponseWrapper.success(createdRole), HttpStatus.CREATED);
        }

        @Operation(summary = "Update an existing role", description = "Updates the details of an existing role by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Role updated successfully.", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "Role not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE:UPDATE')")
        public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> updateRole(
                        @Parameter(description = "ID of the role to update.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
                        @Valid @RequestBody UpdateRoleRequest request) {
                RoleResponseDto updatedRole = roleService.updateRole(id, request);
                return ResponseEntity.ok(ApiResponseWrapper.success(updatedRole));
        }

        @Operation(summary = "Delete a role", description = "Deletes a role by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Role deleted successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "Role not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE:DELETE')")
        public ResponseEntity<ApiResponseWrapper<Object>> deleteRole(
                        @Parameter(description = "ID of the role to delete.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
                roleService.deleteRole(id);
                return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
        }

        @Operation(summary = "Get a role by ID", description = "Retrieves the details of a specific role by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Role found.", content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Role not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE:READ')")
        public ResponseEntity<ApiResponseWrapper<RoleResponseDto>> getRoleById(
                        @Parameter(description = "ID of the role to retrieve.", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {
                RoleResponseDto role = roleService.getRoleById(id);
                return ResponseEntity.ok(ApiResponseWrapper.success(role));
        }

        @Operation(summary = "Get all roles", description = "Retrieves all roles without pagination.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of roles.", content = @Content(schema = @Schema(implementation = RoleListResponse.class)))
        })
        @GetMapping("/all")
        @PreAuthorize("hasAuthority('ROLE:READ')")
        public ResponseEntity<ApiResponseWrapper<List<RoleResponseDto>>> getAllRoles() {
                List<RoleResponseDto> roles = roleService.getAllRoles();
                return ResponseEntity.ok(ApiResponseWrapper.success(roles));
        }

        @Operation(summary = "Search roles", description = "Search and filter roles with pagination support.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated roles.", content = @Content(schema = @Schema(implementation = RolePageResponse.class)))
        })
        @GetMapping("/search")
        @PreAuthorize("hasAuthority('ROLE:READ')")
        public ResponseEntity<ApiResponseWrapper<PageableResponse<RoleResponseDto>>> searchRoles(
                        @Valid @ParameterObject RoleFilterRequest filter) {
                Page<RoleResponseDto> rolesPage = roleService.searchRoles(filter);
                return ResponseEntity.ok(ApiResponseWrapper.success(rolesPage));
        }

        // --- OpenAPI Schema Helper Classes ---

        @Schema(name = "RoleResponse", description = "API response containing a single role object.")
        public static class RoleResponse extends ApiResponseWrapper<RoleResponseDto> {
        }

        @Schema(name = "RoleListResponse", description = "API response containing a list of role objects.")
        public static class RoleListResponse extends ApiResponseWrapper<List<RoleResponseDto>> {
        }

        @Schema(name = "RolePageResponse", description = "API response containing a paginated list of role objects.")
        public static class RolePageResponse extends ApiResponseWrapper<PageableResponse<RoleResponseDto>> {
        }
}
