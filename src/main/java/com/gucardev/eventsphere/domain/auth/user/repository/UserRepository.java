package com.gucardev.eventsphere.domain.auth.user.repository;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.shared.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseJpaRepository<User, UUID> {

        @EntityGraph(attributePaths = { "roles", "roles.permissions" })
        Optional<User> findByEmail(String email);

        @Query("SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN FETCH u.roles r " +
                        "LEFT JOIN FETCH r.permissions " +
                        "WHERE u.email = :email")
        Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

        boolean existsByEmail(String email);

}