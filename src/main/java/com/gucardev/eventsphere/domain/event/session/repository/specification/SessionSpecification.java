package com.gucardev.eventsphere.domain.event.session.repository.specification;

import com.gucardev.eventsphere.domain.event.session.entity.Session;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SessionSpecification extends BaseSpecification {

    public static Specification<Session> withTitle(String title) {
        return BaseSpecification.like("title", title);
    }

    public static Specification<Session> withSpeakerName(String speakerName) {
        return BaseSpecification.like("speakerName", speakerName);
    }

    public static Specification<Session> withEventId(UUID eventId) {
        return (root, query, cb) -> {
            if (eventId == null) return null;
            return cb.equal(root.get("event").get("id"), eventId);
        };
    }

    public static Specification<Session> fetchEvent() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("event", JoinType.LEFT);
            }
            return null;
        };
    }
}
