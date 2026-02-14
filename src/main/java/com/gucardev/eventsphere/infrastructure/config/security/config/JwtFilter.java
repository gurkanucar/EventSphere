package com.gucardev.eventsphere.infrastructure.config.security.config;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.JwtAuthDetails;
import com.gucardev.eventsphere.infrastructure.config.security.service.JwtTokenService;
import com.gucardev.eventsphere.infrastructure.util.EncryptionService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final EncryptionService encryptionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")
                || request.getServletPath().contains("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String encryptedJwt = authorizationHeader.substring(7);
            String jwt = encryptionService.decryptToken(encryptedJwt);

            if (!tokenService.validateToken(jwt)) {
                log.warn("Invalid or expired JWT token for request: {}", request.getRequestURI());
                sendErrorResponse(response, "Invalid or expired token");
                return;
            }

            UserResponseDto userDto = tokenService.extractUserDtoFromToken(jwt);

            // Use JwtAuthDetails for JWT-based authentication (no password required)
            JwtAuthDetails authDetails = new JwtAuthDetails(userDto);

            CustomUsernamePasswordAuthenticationToken authToken =
                    new CustomUsernamePasswordAuthenticationToken(
                            authDetails,
                            authDetails.getAuthorities(),
                            jwt,
                            userDto);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (EncryptionService.TokenEncryptionException e) {
            log.error("Token decryption failed for request: {}", request.getRequestURI(), e);
            sendErrorResponse(response, "Token decryption failed");
            return;
        } catch (JwtException e) {
            log.warn("Invalid JWT token format for request: {}", request.getRequestURI(), e);
            sendErrorResponse(response, "Invalid token format");
            return;
        } catch (Exception e) {
            log.error("Authentication failed for request: {}", request.getRequestURI(), e);
            sendErrorResponse(response, "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message)
        );
    }
}