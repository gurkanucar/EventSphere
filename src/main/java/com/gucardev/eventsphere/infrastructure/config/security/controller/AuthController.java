package com.gucardev.eventsphere.infrastructure.config.security.controller;

import com.gucardev.eventsphere.domain.auth.user.model.dto.UserResponseDto;
import com.gucardev.eventsphere.infrastructure.config.security.dto.request.LoginRequest;
import com.gucardev.eventsphere.infrastructure.config.security.dto.response.TokenDto;
import com.gucardev.eventsphere.infrastructure.config.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.login(loginRequest));
    }

    @GetMapping("/get-myself")
    public ResponseEntity<UserResponseDto> getMyself() {
        return ResponseEntity.ok().body(authService.getAuthenticatedUser());
    }

}
