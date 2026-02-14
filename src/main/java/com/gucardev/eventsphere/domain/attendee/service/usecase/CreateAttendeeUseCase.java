package com.gucardev.eventsphere.domain.attendee.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.mapper.AttendeeMapper;
import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.model.request.CreateAttendeeRequest;
import com.gucardev.eventsphere.domain.attendee.repository.AttendeeRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAttendeeUseCase implements UseCase<CreateAttendeeRequest, AttendeeResponseDto> {

    private final AttendeeRepository attendeeRepository;
    private final UserRepository userRepository;
    private final AttendeeMapper attendeeMapper;

    @Override
    @Transactional
    public AttendeeResponseDto execute(CreateAttendeeRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> ExceptionUtil.notFound("User", request.userId()));

        // Logic check: User should only have one Attendee profile?
        // Assumed yes.
        
        Attendee attendee = attendeeMapper.toEntity(request);
        attendee.setUser(user);

        Attendee savedAttendee = attendeeRepository.save(attendee);
        log.info("Created attendee profile for user: {}", user.getId());

        return attendeeMapper.toDto(savedAttendee);
    }
}
