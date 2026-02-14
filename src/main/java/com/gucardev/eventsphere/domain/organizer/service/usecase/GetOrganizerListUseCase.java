package com.gucardev.eventsphere.domain.organizer.service.usecase;

import com.gucardev.eventsphere.domain.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.organizer.mapper.OrganizerMapper;
import com.gucardev.eventsphere.domain.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.organizer.model.request.OrganizerFilterRequest;
import com.gucardev.eventsphere.domain.organizer.repository.specification.OrganizerSpecification;
import com.gucardev.eventsphere.domain.organizer.repository.OrganizerRepository;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GetOrganizerListUseCase implements UseCase<OrganizerFilterRequest, Page<OrganizerResponseDto>> {

    private final OrganizerRepository organizerRepository;
    private final OrganizerMapper organizerMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizerResponseDto> execute(OrganizerFilterRequest filter) {
        Specification<Organizer> spec = BaseSpecification.toSpec(filter);

        if (StringUtils.hasText(filter.getOrganizationName())) {
            spec = spec.and(OrganizerSpecification.withOrganizationName(filter.getOrganizationName()));
        }
        if (StringUtils.hasText(filter.getContactEmail())) {
            spec = spec.and(OrganizerSpecification.withContactEmail(filter.getContactEmail()));
        }

        // Fetch user to avoid N+1
        spec = spec.and(OrganizerSpecification.fetchUser());

        Pageable pageable = filter.toPageable();
        return organizerRepository.findAll(spec, pageable).map(organizerMapper::toDto);
    }
}
