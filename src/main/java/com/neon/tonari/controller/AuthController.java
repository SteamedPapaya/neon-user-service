package com.neon.tonari.controller;

import com.neon.tonari.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = jwtTokenProvider.generateToken(authentication.getName());
        return ResponseEntity.ok(token);
    }
}