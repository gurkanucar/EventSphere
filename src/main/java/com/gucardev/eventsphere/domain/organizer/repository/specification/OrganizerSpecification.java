package com.gucardev.eventsphere.domain.organizer.repository.specification;

import com.gucardev.eventsphere.domain.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrganizerSpecification extends BaseSpecification {

    public static Specification<Organizer> withOrganizationName(String organizationName) {
        return BaseSpecification.like("organizationName", organizationName);
    }

    public static Specification<Organizer> withContactEmail(String contactEmail) {
        return BaseSpecification.like("contactEmail", contactEmail);
    }

    public static Specification<Organizer> fetchUser() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("user", JoinType.LEFT);
            }
            return null;
        };
    }
}
