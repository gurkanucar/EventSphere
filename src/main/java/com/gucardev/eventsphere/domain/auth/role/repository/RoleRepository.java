package com.gucardev.eventsphere.domain.auth.role.repository;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends BaseJpaRepository<Role, UUID> {

    @EntityGraph(attributePaths = { "permissions" })
    @NotNull
    Optional<Role> findById(@NotNull UUID id);

    @EntityGraph(attributePaths = { "permissions" })
    Optional<Role> findByName(String name);

    Set<Role> findByIdIn(Set<UUID> ids);

    boolean existsByName(String name);
}
