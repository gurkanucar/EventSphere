package com.gucardev.eventsphere.domain.attendee.service.usecase;

import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.mapper.AttendeeMapper;
import com.gucardev.eventsphere.domain.attendee.model.dto.AttendeeResponseDto;
import com.gucardev.eventsphere.domain.attendee.model.request.AttendeeFilterRequest;
import com.gucardev.eventsphere.domain.attendee.repository.specification.AttendeeSpecification;
import com.gucardev.eventsphere.domain.attendee.repository.AttendeeRepository;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAttendeeListUseCase implements UseCase<AttendeeFilterRequest, Page<AttendeeResponseDto>> {

    private final AttendeeRepository attendeeRepository;
    private final AttendeeMapper attendeeMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AttendeeResponseDto> execute(AttendeeFilterRequest filter) {
        Specification<Attendee> spec = BaseSpecification.toSpec(filter);
        
        // Add more filters here if needed

        // Fetch user to avoid N+1
        spec = spec.and(AttendeeSpecification.fetchUser());

        Pageable pageable = filter.toPageable();
        return attendeeRepository.findAll(spec, pageable).map(attendeeMapper::toDto);
    }
}
