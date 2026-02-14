package com.gucardev.eventsphere.domain.auth.role.service;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.mapper.RoleMapper;
import com.gucardev.eventsphere.domain.auth.role.model.dto.RoleResponseDto;
import com.gucardev.eventsphere.domain.auth.role.model.request.CreateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.RoleFilterRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.UpdateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.role.repository.specification.RoleSpecification;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing roles and their permissions.
 * Follows the backend skill guidelines with guard clauses, proper logging, and
 * exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    /**
     * Create a new role with optional permissions.
     */
    @Transactional
    public RoleResponseDto createRole(CreateRoleRequest request) {
        log.debug("Creating role with name: {}", request.getName());

        // Guard clause: validate uniqueness
        if (roleRepository.existsByName(request.getName().toUpperCase())) {
            throw ExceptionUtil.alreadyExists("Role", "name", request.getName());
        }

        // Create role entity
        Role role = new Role();
        role.setName(request.getName()); // setName handles ROLE_ prefix
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());

        // Assign permissions if provided
        if (!CollectionUtils.isEmpty(request.getPermissionIds())) {
            assignPermissions(role, request.getPermissionIds());
        }

        // Save and return
        Role savedRole = roleRepository.save(role);
        log.info("Successfully created role: {} (ID: {})", savedRole.getName(), savedRole.getId());

        return roleMapper.toDto(savedRole);
    }

    /**
     * Update an existing role.
     */
    @Transactional
    public RoleResponseDto updateRole(UUID roleId, UpdateRoleRequest request) {
        log.debug("Updating role with ID: {}", roleId);

        // Guard clause: find role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ExceptionUtil.notFound("Role", roleId));

        // Update name if provided
        if (StringUtils.hasText(request.getName())) {
            // Check uniqueness if name is changing
            if (!role.getName().equalsIgnoreCase(request.getName()) &&
                    roleRepository.existsByName(request.getName().toUpperCase())) {
                throw ExceptionUtil.alreadyExists("Role", "name", request.getName());
            }
            role.setName(request.getName());
        }

        // Update other fields
        if (StringUtils.hasText(request.getDisplayName())) {
            role.setDisplayName(request.getDisplayName());
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        // Update permissions if provided
        if (request.getPermissionIds() != null) {
            assignPermissions(role, request.getPermissionIds());
        }

        // Save and return
        Role updatedRole = roleRepository.save(role);
        log.info("Successfully updated role: {} (ID: {})", updatedRole.getName(), updatedRole.getId());

        return roleMapper.toDto(updatedRole);
    }

    /**
     * Get a role by ID.
     * Simple read operation - no @Transactional needed (repository is already
     * transactional)
     */
    public RoleResponseDto getRoleById(UUID roleId) {
        log.debug("Fetching role with ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ExceptionUtil.notFound("Role", roleId));

        log.info("Successfully retrieved role: {}", role.getName());

        return roleMapper.toDto(role);
    }

    /**
     * Get all roles.
     * Simple read operation - no @Transactional needed
     */
    public List<RoleResponseDto> getAllRoles() {
        log.debug("Fetching all roles");

        List<Role> roles = roleRepository.findAll();

        log.info("Retrieved {} roles", roles.size());

        return roles.stream()
                .map(roleMapper::toDto)
                .toList();
    }

    /**
     * Search roles with filtering and pagination.
     */
    public Page<RoleResponseDto> searchRoles(RoleFilterRequest filter) {
        log.debug("Searching roles with filter: {}", filter);

        // Construct pageable
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDir(), filter.getSortBy()));

        // Build dynamic specification
        Specification<Role> spec = buildSpecification(filter);

        // Fetch and map
        Page<Role> rolesPage = roleRepository.findAll(spec, pageable);

        log.info("Retrieved {} roles (page {} of {})",
                rolesPage.getNumberOfElements(),
                rolesPage.getNumber() + 1,
                rolesPage.getTotalPages());

        return rolesPage.map(roleMapper::toDto);
    }

    /**
     * Delete a role by ID.
     */
    @Transactional
    public void deleteRole(UUID roleId) {
        log.debug("Deleting role with ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> ExceptionUtil.notFound("Role", roleId));

        roleRepository.delete(role);

        log.info("Successfully deleted role: {} (ID: {})", role.getName(), roleId);
    }

    // --- Private Helper Methods ---

    private void assignPermissions(Role role, Set<UUID> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            role.getPermissions().clear();
            return;
        }

        Set<Permission> permissions = permissionRepository.findByIdIn(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            log.warn("Could not find all permissions for IDs: {}", permissionIds);
            throw ExceptionUtil.notFound("One or more permissions", permissionIds);
        }

        role.setPermissions(permissions);
    }

    private Specification<Role> buildSpecification(RoleFilterRequest filter) {
        // Start with neutral specification
        Specification<Role> spec = (root, query, cb) -> null;

        // Chain role-specific filters
        spec = spec
                .and(RoleSpecification.hasNameLike(filter.getName()))
                .and(RoleSpecification.hasDisplayNameLike(filter.getDisplayName()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()));

        return spec;
    }
}
