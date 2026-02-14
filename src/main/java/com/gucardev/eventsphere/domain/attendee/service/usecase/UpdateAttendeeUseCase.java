package com.gucardev.eventsphere.domain.attendee.service.usecase;

import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.mapper.AttendeeMapper;
import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.model.parameter.UpdateAttendeeUseCaseParam;
import com.gucardev.eventsphere.domain.attendee.repository.AttendeeRepository;
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
public class UpdateAttendeeUseCase implements UseCase<UpdateAttendeeUseCaseParam, AttendeeResponseDto> {

    private final AttendeeRepository attendeeRepository;
    private final AttendeeMapper attendeeMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public AttendeeResponseDto execute(UpdateAttendeeUseCaseParam param) {
        Attendee attendee = attendeeRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Attendee", param.id()));

        // Validate owner
        ownershipValidator.validateOwnership(attendee.getUser().getId());

        attendeeMapper.updateEntityFromRequest(param.request(), attendee);

        Attendee updatedAttendee = attendeeRepository.save(attendee);
        log.info("Updated attendee: {}", updatedAttendee.getId());

        return attendeeMapper.toDto(updatedAttendee);
    }
}
