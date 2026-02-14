package com.gucardev.eventsphere.domain.shared.repository;

import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import com.gucardev.eventsphere.domain.shared.enumeration.DeletedStatus;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void softDelete(@Param("id") ID id, @Param("reason") String reason);

    default Optional<T> findById(ID id, DeletedStatus deletedStatus) {
        Specification<T> spec = BaseSpecification.<T, ID>byId(id)
                .and(BaseSpecification.deleted(deletedStatus));
        return findOne(spec);
    }

}
