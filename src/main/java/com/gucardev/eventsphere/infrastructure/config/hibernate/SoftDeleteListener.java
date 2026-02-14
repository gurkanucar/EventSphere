package com.gucardev.eventsphere.infrastructure.config.hibernate;

import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import com.gucardev.eventsphere.infrastructure.config.BeanUtil;
import jakarta.persistence.PreRemove;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;

public class SoftDeleteListener {

    @PreRemove
    public void preRemove(BaseEntity entity) {
        entity.setDeletedAt(LocalDateTime.now());
        @SuppressWarnings("unchecked")
        AuditorAware<String> auditorAware = (AuditorAware<String>) BeanUtil.getBean(AuditorAware.class);
        // Set the 'deletedBy' field with the current user's name
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }
}