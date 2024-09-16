package com.neon.tonari.security;

import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.User;
import com.neon.tonari.entity.RoleType; // RoleType 추가 필요
import com.neon.tonari.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OAuth2LoginTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // 테스트 환경을 초기화하기 위해 모든 사용자 삭제

        // Given: 사용자 정보 저장
        User user = new User(null, "testuser@example.com", "", ProviderType.GOOGLE, "Test User", "google-sub-id", RoleType.USER);
        userRepository.save(user);

        // When: JWT 토큰 발급
        ResponseEntity<String> response = restTemplate.getForEntity("/api/auth/token", String.class);
        jwtToken = response.getBody(); // 실제 JWT 토큰 저장
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void whenLoginWithGoogle_thenRedirectToOAuthProvider() throws Exception {
        // When & Then
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void whenGetUserProfile_thenSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }
}