package com.gucardev.eventsphere.domain.auth.user.repository.specification;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for User entity dynamic filtering.
 * Extends BaseSpecification for common operations (like, equals,
 * createdBetween, etc.)
 */
public class UserSpecification extends BaseSpecification {

    /**
     * Filter users by email (case-insensitive partial match)
     */
    public static Specification<User> hasEmailLike(String email) {
        return BaseSpecification.like("email", email);
    }

    /**
     * Filter users by name (case-insensitive partial match)
     */
    public static Specification<User> hasNameLike(String name) {
        return BaseSpecification.like("name", name);
    }

    /**
     * Filter users by surname (case-insensitive partial match)
     */
    public static Specification<User> hasSurnameLike(String surname) {
        return BaseSpecification.like("surname", surname);
    }

    /**
     * Filter users by phone number (partial match)
     */
    public static Specification<User> hasPhoneNumberLike(String phoneNumber) {
        return BaseSpecification.like("phoneNumber", phoneNumber);
    }

    /**
     * Filter users by activation status
     */
    public static Specification<User> isActivated(Boolean activated) {
        return (root, query, cb) -> {
            if (activated == null)
                return null;
            return cb.equal(root.get("activated"), activated);
        };
    }
}
