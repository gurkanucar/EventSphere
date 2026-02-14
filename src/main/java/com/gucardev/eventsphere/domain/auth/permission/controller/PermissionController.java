package com.gucardev.eventsphere.domain.auth.permission.controller;

import com.gucardev.eventsphere.domain.auth.permission.model.dto.PermissionResponseDto;
import com.gucardev.eventsphere.domain.auth.permission.model.request.CreatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.PermissionFilterRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.UpdatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.service.PermissionService;
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
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for creating, reading, updating, and deleting permissions.")
public class PermissionController {

        private final PermissionService permissionService;

        @Operation(summary = "Create a new permission", description = "Creates a new permission with action and resource.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Permission created successfully.", content = @Content(schema = @Schema(implementation = PermissionResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "409", description = "Permission with this action-resource combination already exists.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PostMapping
        @PreAuthorize("hasAuthority('PERMISSION:CREATE')")
        public ResponseEntity<ApiResponseWrapper<PermissionResponseDto>> createPermission(
                        @Valid @RequestBody CreatePermissionRequest request) {
                PermissionResponseDto createdPermission = permissionService.createPermission(request);
                return new ResponseEntity<>(ApiResponseWrapper.success(createdPermission), HttpStatus.CREATED);
        }

        @Operation(summary = "Update an existing permission", description = "Updates the details of an existing permission by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Permission updated successfully.", content = @Content(schema = @Schema(implementation = PermissionResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request body.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "Permission not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION:UPDATE')")
        public ResponseEntity<ApiResponseWrapper<PermissionResponseDto>> updatePermission(
                        @Parameter(description = "ID of the permission to update.", required = true, example = "550e8400-e29b-41d4-a716-446655440001") @PathVariable UUID id,
                        @Valid @RequestBody UpdatePermissionRequest request) {
                PermissionResponseDto updatedPermission = permissionService.updatePermission(id, request);
                return ResponseEntity.ok(ApiResponseWrapper.success(updatedPermission));
        }

        @Operation(summary = "Delete a permission", description = "Deletes a permission by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Permission deleted successfully.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class))),
                        @ApiResponse(responseCode = "404", description = "Permission not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION:DELETE')")
        public ResponseEntity<ApiResponseWrapper<Object>> deletePermission(
                        @Parameter(description = "ID of the permission to delete.", required = true, example = "550e8400-e29b-41d4-a716-446655440001") @PathVariable UUID id) {
                permissionService.deletePermission(id);
                return ResponseEntity.ok(ApiResponseWrapper.successWithEmptyData());
        }

        @Operation(summary = "Get a permission by ID", description = "Retrieves the details of a specific permission by its ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Permission found.", content = @Content(schema = @Schema(implementation = PermissionResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Permission not found.", content = @Content(schema = @Schema(implementation = ApiResponseWrapper.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION:READ')")
        public ResponseEntity<ApiResponseWrapper<PermissionResponseDto>> getPermissionById(
                        @Parameter(description = "ID of the permission to retrieve.", required = true, example = "550e8400-e29b-41d4-a716-446655440001") @PathVariable UUID id) {
                PermissionResponseDto permission = permissionService.getPermissionById(id);
                return ResponseEntity.ok(ApiResponseWrapper.success(permission));
        }

        @Operation(summary = "Get all permissions", description = "Retrieves all permissions without pagination.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of permissions.", content = @Content(schema = @Schema(implementation = PermissionListResponse.class)))
        })
        @GetMapping("/all")
        @PreAuthorize("hasAuthority('PERMISSION:READ')")
        public ResponseEntity<ApiResponseWrapper<List<PermissionResponseDto>>> getAllPermissions() {
                List<PermissionResponseDto> permissions = permissionService.getAllPermissions();
                return ResponseEntity.ok(ApiResponseWrapper.success(permissions));
        }

        @Operation(summary = "Search permissions", description = "Search and filter permissions with pagination support.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated permissions.", content = @Content(schema = @Schema(implementation = PermissionPageResponse.class)))
        })
        @GetMapping("/search")
        @PreAuthorize("hasAuthority('PERMISSION:READ')")
        public ResponseEntity<ApiResponseWrapper<PageableResponse<PermissionResponseDto>>> searchPermissions(
                        @Valid @ParameterObject PermissionFilterRequest filter) {
                Page<PermissionResponseDto> permissionsPage = permissionService.searchPermissions(filter);
                return ResponseEntity.ok(ApiResponseWrapper.success(permissionsPage));
        }

        // --- OpenAPI Schema Helper Classes ---

        @Schema(name = "PermissionResponse", description = "API response containing a single permission object.")
        public static class PermissionResponse extends ApiResponseWrapper<PermissionResponseDto> {
        }

        @Schema(name = "PermissionListResponse", description = "API response containing a list of permission objects.")
        public static class PermissionListResponse extends ApiResponseWrapper<List<PermissionResponseDto>> {
        }

        @Schema(name = "PermissionPageResponse", description = "API response containing a paginated list of permission objects.")
        public static class PermissionPageResponse extends ApiResponseWrapper<PageableResponse<PermissionResponseDto>> {
        }
}
