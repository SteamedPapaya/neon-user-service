package com.neon.tonari.controller;

import com.neon.tonari.security.jwt.JwtTokenProvider;
import com.neon.tonari.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = jwtTokenProvider.generateToken(authentication.getName());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        String email = authentication.getName();
        authService.deleteRefreshToken(email); // 리프레시 토큰 삭제
        log.info("Logged out user {}", email);
        return ResponseEntity.ok().build();
    }
}