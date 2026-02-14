package com.gucardev.eventsphere.domain.attendee.service.usecase;

import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.mapper.AttendeeMapper;
import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.repository.AttendeeRepository;
import com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAttendeeUseCase implements UseCase<UUID, AttendeeResponseDto> {

    private final AttendeeRepository attendeeRepository;
    private final AttendeeMapper attendeeMapper;

    @Override
    @Transactional(readOnly = true)
    public AttendeeResponseDto execute(UUID id) {
        Attendee attendee = attendeeRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.notFound("Attendee", id));
        return attendeeMapper.toDto(attendee);
    }
}
