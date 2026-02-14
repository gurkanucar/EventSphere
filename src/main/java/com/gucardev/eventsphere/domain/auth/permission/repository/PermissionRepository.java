package com.gucardev.eventsphere.domain.auth.permission.repository;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends BaseJpaRepository<Permission, UUID> {

    Set<Permission> findByIdIn(Set<UUID> ids);

    Optional<Permission> findByActionAndResource(String action, String resource);

    boolean existsByActionAndResource(String action, String resource);
}
