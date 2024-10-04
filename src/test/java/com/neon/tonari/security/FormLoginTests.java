package com.neon.tonari.security;

import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.User;
import com.neon.tonari.entity.RoleType;
import com.neon.tonari.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FormLoginTests {

    @Autowired
    private MockMvc mockMvc;

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
    void whenFormLogin_thenLoadUserDetailsService() throws Exception {
        // 폼 기반 로그인을 통해 사용자가 성공적으로 인증되었는지 확인
        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }
}