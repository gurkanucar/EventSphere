package com.gucardev.eventsphere.domain.auth.role.repository.specification;

import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for Role entity dynamic filtering.
 */
public class RoleSpecification extends BaseSpecification {

    /**
     * Filter roles by name (case-insensitive partial match)
     */
    public static Specification<Role> hasNameLike(String name) {
        return BaseSpecification.like("name", name);
    }

    /**
     * Filter roles by display name (case-insensitive partial match)
     */
    public static Specification<Role> hasDisplayNameLike(String displayName) {
        return BaseSpecification.like("displayName", displayName);
    }
}
