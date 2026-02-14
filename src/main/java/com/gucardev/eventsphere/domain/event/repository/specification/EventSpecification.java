package com.gucardev.eventsphere.domain.event.repository.specification;

import com.gucardev.eventsphere.domain.event.entity.Event;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification extends BaseSpecification {

    public static Specification<Event> withTitle(String title) {
        return BaseSpecification.like("title", title);
    }

    public static Specification<Event> withLocation(String location) {
        return BaseSpecification.like("location", location);
    }

    public static Specification<Event> isPublished(Boolean isPublished) {
        return BaseSpecification.equals("isPublished", isPublished);
    }

    public static Specification<Event> fetchOrganizer() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) { // Avoid fetching in count queries
                root.fetch("organizer", JoinType.LEFT);
            }
            return null;
        };
    }
}
