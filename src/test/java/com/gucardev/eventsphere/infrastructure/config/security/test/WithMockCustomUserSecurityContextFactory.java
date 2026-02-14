package com.gucardev.eventsphere.infrastructure.config.security.test;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.config.CustomUsernamePasswordAuthenticationToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<@NotNull WithMockCustomUser> {

    @Override
    public @NotNull SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<GrantedAuthority> authorities = new java.util.ArrayList<>();

        // Add roles
        Arrays.stream(customUser.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .forEach(authorities::add);

        // Add explicit authorities
        Arrays.stream(customUser.authorities())
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        UserDetails principal = new User(customUser.email(), "password", authorities);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(UUID.randomUUID());
        userDto.setEmail(customUser.email());
        userDto.setName(customUser.username());
        userDto.setSurname("Test");

        // Populate other fields as needed for the DTO

        CustomUsernamePasswordAuthenticationToken auth = new CustomUsernamePasswordAuthenticationToken(
                principal,
                authorities,
                customUser.jwtToken(),
                userDto);

        context.setAuthentication(auth);
        return context;
    }
}
