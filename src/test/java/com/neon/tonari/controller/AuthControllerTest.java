package com.neon.tonari.controller;

import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.RoleType;
import com.neon.tonari.entity.User;
import com.neon.tonari.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void whenGetToken_thenReturnToken() {
        // Given
        User user = new User(null, "testuser@example.com", "", ProviderType.GOOGLE, "Test User", "google-sub-id", RoleType.USER);
        userRepository.save(user);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity("/api/auth/token", String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
