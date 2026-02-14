package com.gucardev.eventsphere.domain.event.attendee.repository.specification;

import com.gucardev.eventsphere.domain.event.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class AttendeeSpecification extends BaseSpecification {

    public static Specification<Attendee> fetchUser() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("user", JoinType.LEFT);
            }
            return null;
        };
    }
}
