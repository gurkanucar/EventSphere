package com.gucardev.eventsphere.domain.organizer.service.usecase;

import com.gucardev.eventsphere.domain.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.organizer.mapper.OrganizerMapper;
import com.gucardev.eventsphere.domain.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.organizer.repository.OrganizerRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrganizerUseCase implements UseCase<UUID, OrganizerResponseDto> {

    private final OrganizerRepository organizerRepository;
    private final OrganizerMapper organizerMapper;

    @Override
    @Transactional(readOnly = true)
    public OrganizerResponseDto execute(UUID id) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Organizer", id));
        return organizerMapper.toDto(organizer);
    }
}
