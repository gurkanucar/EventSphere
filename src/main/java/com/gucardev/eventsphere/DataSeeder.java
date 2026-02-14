package com.gucardev.eventsphere;


import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    // Define standard constants
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @PostConstruct
    @Transactional
    public void seed() {
        log.info("Checking for initial data seeding...");

        if (roleRepository.count() == 0) {
            log.info("No roles found. seeding default data...");
            seedData();
        } else {
            log.info("Data already exists. Skipping seed.");
        }
    }

    private void seedData() {
        // 1. Create Permissions
        Map<String, Permission> perms = new HashMap<>();

        // Define resources and actions
        perms.put("USER_READ", createPermissionIfNotFound("READ", "USER", "Read Users", "Can view user details"));
        perms.put("USER_WRITE", createPermissionIfNotFound("WRITE", "USER", "Edit Users", "Can create or edit users"));
        perms.put("USER_DELETE", createPermissionIfNotFound("DELETE", "USER", "Delete Users", "Can remove users"));

        perms.put("ROLE_READ", createPermissionIfNotFound("READ", "ROLE", "Read Roles", "Can view roles"));
        perms.put("ROLE_WRITE", createPermissionIfNotFound("WRITE", "ROLE", "Edit Roles", "Can modify roles"));

        // 2. Create Roles
        Role adminRole = createRoleIfNotFound(ROLE_ADMIN, "Administrator", "System Administrator", new HashSet<>(perms.values()));

        // User only gets read permissions
        Set<Permission> userPerms = Set.of(perms.get("USER_READ"));
        Role userRole = createRoleIfNotFound(ROLE_USER, "Standard User", "Regular application user", userPerms);

        // 3. Create Users
        createUserIfNotFound("admin@mail.com", "Admin", "Super", "password", Set.of(adminRole));
        createUserIfNotFound("user@mail.com", "John", "Doe", "password", Set.of(userRole));

        log.info("Seeding completed successfully.");
    }

    private Permission createPermissionIfNotFound(String action, String resource, String displayName, String desc) {
        return permissionRepository.findByActionAndResource(action, resource)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setAction(action);
                    p.setResource(resource);
                    p.setDisplayName(displayName);
                    p.setDescription(desc);
                    return permissionRepository.save(p);
                });
    }

    private Role createRoleIfNotFound(String name, String displayName, String desc, Set<Permission> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(name); // Setter handles ROLE_ prefix logic automatically if you kept that logic
                    r.setDisplayName(displayName);
                    r.setDescription(desc);
                    r.setPermissions(permissions);
                    return roleRepository.save(r);
                });
    }

    private void createUserIfNotFound(String email, String name, String surname, String password, Set<Role> roles) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setSurname(surname);
            user.setPassword(passwordEncoder.encode(password));
            user.setActivated(true);
            user.setRoles(roles);
            userRepository.save(user);
            log.info("Created user: {}", email);
        }
    }
}