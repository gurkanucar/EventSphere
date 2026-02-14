package com.gucardev.eventsphere.domain.auth.permission.service;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.mapper.PermissionMapper;
import com.gucardev.eventsphere.domain.auth.permission.model.dto.PermissionResponseDto;
import com.gucardev.eventsphere.domain.auth.permission.model.request.CreatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.PermissionFilterRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.UpdatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import com.gucardev.eventsphere.domain.auth.permission.repository.specification.PermissionSpecification;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing permissions.
 * Follows the backend skill guidelines with guard clauses, proper logging, and
 * exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

        private final PermissionRepository permissionRepository;
        private final PermissionMapper permissionMapper;

        /**
         * Create a new permission.
         */
        @Transactional
        public PermissionResponseDto createPermission(CreatePermissionRequest request) {
                log.debug("Creating permission: {} on {}", request.getAction(), request.getResource());

                // Guard clause: validate uniqueness
                if (permissionRepository.existsByActionAndResource(
                                request.getAction().toUpperCase(java.util.Locale.ROOT),
                                request.getResource().toUpperCase(java.util.Locale.ROOT))) {
                        throw ExceptionUtil.alreadyExists("Permission",
                                        "action-resource combination",
                                        request.getAction() + ":" + request.getResource());
                }

                // Create permission entity
                Permission permission = new Permission();
                permission.setAction(request.getAction().toUpperCase(java.util.Locale.ROOT));
                permission.setResource(request.getResource().toUpperCase(java.util.Locale.ROOT));
                permission.setDisplayName(request.getDisplayName());
                permission.setDescription(request.getDescription());

                // Save and return
                Permission savedPermission = permissionRepository.save(permission);
                log.info("Successfully created permission: {}:{} (ID: {})",
                                savedPermission.getAction(),
                                savedPermission.getResource(),
                                savedPermission.getId());

                return permissionMapper.toDto(savedPermission);
        }

        /**
         * Update an existing permission.
         */
        @Transactional
        public PermissionResponseDto updatePermission(UUID permissionId, UpdatePermissionRequest request) {
                log.debug("Updating permission with ID: {}", permissionId);

                // Guard clause: find permission
                Permission permission = permissionRepository.findById(permissionId)
                                .orElseThrow(() -> ExceptionUtil.notFound("Permission", permissionId));

                // Update action/resource if provided
                if (StringUtils.hasText(request.getAction()) || StringUtils.hasText(request.getResource())) {
                        String newAction = StringUtils.hasText(request.getAction())
                                        ? request.getAction().toUpperCase(java.util.Locale.ROOT)
                                        : permission.getAction();
                        String newResource = StringUtils.hasText(request.getResource())
                                        ? request.getResource().toUpperCase(java.util.Locale.ROOT)
                                        : permission.getResource();

                        // Check uniqueness if action/resource is changing
                        if (!permission.getAction().equals(newAction)
                                        || !permission.getResource().equals(newResource)) {
                                if (permissionRepository.existsByActionAndResource(newAction, newResource)) {
                                        throw ExceptionUtil.alreadyExists("Permission",
                                                        "action-resource combination",
                                                        newAction + ":" + newResource);
                                }
                                permission.setAction(newAction);
                                permission.setResource(newResource);
                        }
                }

                // Update other fields
                if (StringUtils.hasText(request.getDisplayName())) {
                        permission.setDisplayName(request.getDisplayName());
                }

                if (request.getDescription() != null) {
                        permission.setDescription(request.getDescription());
                }

                // Save and return
                Permission updatedPermission = permissionRepository.save(permission);
                log.info("Successfully updated permission: {}:{} (ID: {})",
                                updatedPermission.getAction(),
                                updatedPermission.getResource(),
                                updatedPermission.getId());

                return permissionMapper.toDto(updatedPermission);
        }

        /**
         * Get a permission by ID.
         * Simple read operation - no @Transactional needed
         */
        public PermissionResponseDto getPermissionById(UUID permissionId) {
                log.debug("Fetching permission with ID: {}", permissionId);

                Permission permission = permissionRepository.findById(permissionId)
                                .orElseThrow(() -> ExceptionUtil.notFound("Permission", permissionId));

                log.info("Successfully retrieved permission: {}:{}", permission.getAction(), permission.getResource());

                return permissionMapper.toDto(permission);
        }

        /**
         * Get all permissions.
         * Simple read operation - no @Transactional needed
         */
        public List<PermissionResponseDto> getAllPermissions() {
                log.debug("Fetching all permissions");

                List<Permission> permissions = permissionRepository.findAll();

                log.info("Retrieved {} permissions", permissions.size());

                return permissions.stream()
                                .map(permissionMapper::toDto)
                                .toList();
        }

        /**
         * Search permissions with filtering and pagination.
         */
        public Page<PermissionResponseDto> searchPermissions(PermissionFilterRequest filter) {
                log.debug("Searching permissions with filter: {}", filter);

                // Construct pageable
                Pageable pageable = PageRequest.of(
                                filter.getPage(),
                                filter.getSize(),
                                Sort.by(filter.getSortDir(), filter.getSortBy()));

                // Build dynamic specification
                Specification<Permission> spec = buildSpecification(filter);

                // Fetch and map
                Page<Permission> permissionsPage = permissionRepository.findAll(spec, pageable);

                log.info("Retrieved {} permissions (page {} of {})",
                                permissionsPage.getNumberOfElements(),
                                permissionsPage.getNumber() + 1,
                                permissionsPage.getTotalPages());

                return permissionsPage.map(permissionMapper::toDto);
        }

        /**
         * Delete a permission by ID.
         */
        @Transactional
        public void deletePermission(UUID permissionId) {
                log.debug("Deleting permission with ID: {}", permissionId);

                Permission permission = permissionRepository.findById(permissionId)
                                .orElseThrow(() -> ExceptionUtil.notFound("Permission", permissionId));

                permissionRepository.delete(permission);

                log.info("Successfully deleted permission: {}:{} (ID: {})",
                                permission.getAction(),
                                permission.getResource(),
                                permissionId);
        }

        // --- Private Helper Methods ---

        private Specification<Permission> buildSpecification(PermissionFilterRequest filter) {
                // Start with neutral specification
                Specification<Permission> spec = (root, query, cb) -> null;

                // Chain permission-specific filters
                spec = spec
                                .and(PermissionSpecification.hasActionLike(filter.getAction()))
                                .and(PermissionSpecification.hasResourceLike(filter.getResource()))
                                .and(PermissionSpecification.hasDisplayNameLike(filter.getDisplayName()))
                                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));

                return spec;
        }
}
