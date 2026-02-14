package com.gucardev.eventsphere.infrastructure.config.security.test;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation to setup a SecurityContext with a
 * CustomUsernamePasswordAuthenticationToken.
 * This is useful for tests that require specific fields from the
 * CustomUsernamePasswordAuthenticationToken
 * that are not present in the standard Spring Security Authentication.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";

    String email() default "user@example.com";

    String[] roles() default { "USER" };

    String[] authorities() default {};

    String jwtToken() default "mock-jwt-token";
}
