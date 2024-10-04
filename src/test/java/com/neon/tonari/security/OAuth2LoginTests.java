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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    public void setUp() throws Exception {
        userRepository.deleteAll(); // 테스트 환경을 초기화하기 위해 모든 사용자 삭제

        // Given: 사용자 정보 저장
        User user = new User(null, "testuser@example.com", "", ProviderType.GOOGLE, "Test User", "google-sub-id", RoleType.USER);
        userRepository.save(user);

        // When: MockMvc를 통해 JWT 토큰 발급 요청 시뮬레이션
        MvcResult result = mockMvc.perform(get("/auth/token")
                        .with(user("testuser@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = result.getResponse().getContentAsString();
    }

    @Test
    void whenLoginWithGoogle_thenRedirectToOAuthProvider() throws Exception {
        // When & Then
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void whenGetUserProfile_thenSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }
}