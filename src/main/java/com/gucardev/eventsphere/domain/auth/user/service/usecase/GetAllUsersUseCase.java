package com.gucardev.eventsphere.domain.auth.user.service.usecase;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.mapper.UserMapper;
import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.domain.auth.user.model.request.UserFilterRequest;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.domain.auth.user.repository.specification.UserSpecification;
import com.gucardev.eventsphere.domain.shared.repository.specification.BaseSpecification;
import com.gucardev.eventsphere.infrastructure.usecase.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase implements UseCase<UserFilterRequest, Page<UserResponseDto>> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserResponseDto> execute(UserFilterRequest filter) {
        log.debug("Fetching users with filter: {}", filter);

        // Construct pageable
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDir(), filter.getSortBy()));

        // Build dynamic specification
        Specification<User> spec = buildSpecification(filter);

        // Fetch and map
        Page<User> usersPage = userRepository.findAll(spec, pageable);

        log.info("Retrieved {} users (page {} of {})",
                usersPage.getNumberOfElements(),
                usersPage.getNumber() + 1,
                usersPage.getTotalPages());

        return usersPage.map(userMapper::toUserResponseDto);
    }

    private Specification<User> buildSpecification(UserFilterRequest filter) {
        // Start with neutral specification
        Specification<User> spec = (root, query, cb) -> null;

        // Chain user-specific filters
        spec = spec
                .and(UserSpecification.hasEmailLike(filter.getEmail()))
                .and(UserSpecification.hasNameLike(filter.getName()))
                .and(UserSpecification.hasSurnameLike(filter.getSurname()))
                .and(UserSpecification.hasPhoneNumberLike(filter.getPhoneNumber()))
                .and(UserSpecification.isActivated(filter.getActivated()))
                .and(BaseSpecification.createdBetween(filter.getStartDate(), filter.getEndDate()))
                .and(UserSpecification.fetchRoles());

        return spec;
    }
}
