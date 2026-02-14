package com.gucardev.eventsphere.infrastructure.config.security.service;

import com.gucardev.eventsphere.domain.auth.user.service.usecase.GetOptionalUserDtoByEmailUseCase;
import com.gucardev.eventsphere.infrastructure.config.security.dto.PasswordAuthDetails;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads user details for password-based authentication.
 * Used by Spring Security's AuthenticationManager during login.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final GetOptionalUserDtoByEmailUseCase getOptionalUserDtoByEmailUseCase;

    @Override
    public @NotNull UserDetails loadUserByUsername(@NotNull String email) throws UsernameNotFoundException {
        return new PasswordAuthDetails(getOptionalUserDtoByEmailUseCase.execute(email)
                .orElseThrow(() -> new UsernameNotFoundException(email)));
    }
}