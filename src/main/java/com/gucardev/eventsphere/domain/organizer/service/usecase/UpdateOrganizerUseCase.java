package com.gucardev.eventsphere.domain.organizer.service.usecase;

import com.gucardev.eventsphere.domain.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.organizer.mapper.OrganizerMapper;
import com.gucardev.eventsphere.domain.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.organizer.model.parameter.UpdateOrganizerUseCaseParam;
import com.gucardev.eventsphere.domain.organizer.repository.OrganizerRepository;
import com.gucardev.eventsphere.domain.shared.util.ResourceOwnershipValidator;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrganizerUseCase implements UseCase<UpdateOrganizerUseCaseParam, OrganizerResponseDto> {

    private final OrganizerRepository organizerRepository;
    private final OrganizerMapper organizerMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public OrganizerResponseDto execute(UpdateOrganizerUseCaseParam param) {
        Organizer organizer = organizerRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Organizer", param.id()));

        // Validate that the user updating this organizer is the owner (the user linked to it) or admin
        ownershipValidator.validateOwnership(organizer.getUser().getId());

        organizerMapper.updateEntityFromRequest(param.request(), organizer);

        Organizer updatedOrganizer = organizerRepository.save(organizer);
        log.info("Updated organizer: {}", updatedOrganizer.getId());

        return organizerMapper.toDto(updatedOrganizer);
    }
}
