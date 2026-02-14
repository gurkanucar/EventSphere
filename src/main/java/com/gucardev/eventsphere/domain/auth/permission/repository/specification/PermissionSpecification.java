package com.gucardev.eventsphere.domain.auth.permission.repository.specification;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for Permission entity dynamic filtering.
 */
public class PermissionSpecification extends BaseSpecification {

    /**
     * Filter permissions by action (case-insensitive partial match)
     */
    public static Specification<Permission> hasActionLike(String action) {
        return BaseSpecification.like("action", action, java.util.Locale.ROOT);
    }

    /**
     * Filter permissions by resource (case-insensitive partial match)
     */
    public static Specification<Permission> hasResourceLike(String resource) {
        return BaseSpecification.like("resource", resource, java.util.Locale.ROOT);
    }

    /**
     * Filter permissions by display name (case-insensitive partial match)
     */
    public static Specification<Permission> hasDisplayNameLike(String displayName) {
        return BaseSpecification.like("displayName", displayName);
    }
}
