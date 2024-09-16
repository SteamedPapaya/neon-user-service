package com.neon.tonari.service;

import com.neon.tonari.entity.User;
import com.neon.tonari.repository.UserRepository;
import com.neon.tonari.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    public String generateRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String refreshToken = UUID.randomUUID().toString();
        String key = REFRESH_TOKEN_PREFIX + user.getEmail();

        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiry()));

        return refreshToken;
    }

    @Transactional
    public String refreshAccessToken(String refreshToken, String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String storedToken = (String) redisTemplate.opsForValue().get(key);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        return jwtTokenProvider.generateToken(email);
    }

    public void deleteRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
    }
}