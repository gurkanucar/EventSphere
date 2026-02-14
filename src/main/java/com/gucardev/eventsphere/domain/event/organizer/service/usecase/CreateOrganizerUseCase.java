package com.gucardev.eventsphere.domain.event.organizer.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.domain.event.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.event.organizer.mapper.OrganizerMapper;
import com.gucardev.eventsphere.domain.event.organizer.model.dto.OrganizerResponseDto;
import com.gucardev.eventsphere.domain.event.organizer.model.request.CreateOrganizerRequest;
import com.gucardev.eventsphere.domain.event.organizer.repository.OrganizerRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrganizerUseCase implements UseCase<CreateOrganizerRequest, OrganizerResponseDto> {

    private final OrganizerRepository organizerRepository;
    private final UserRepository userRepository;
    private final OrganizerMapper organizerMapper;

    @Override
    @Transactional
    public OrganizerResponseDto execute(CreateOrganizerRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> ExceptionUtil.notFound("User", request.userId()));

        // Logic check: User should only have one Organizer profile?
        // DB constraint unique=true on user_id should handle this, or we can check here.
        
        Organizer organizer = organizerMapper.toEntity(request);
        organizer.setUser(user);

        Organizer savedOrganizer = organizerRepository.save(organizer);
        log.info("Created organizer profile for user: {}", user.getId());

        return organizerMapper.toDto(savedOrganizer);
    }
}
