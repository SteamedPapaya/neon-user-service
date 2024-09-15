package com.neon.tonari.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * User 엔티티의 동작을 검증하기 위한 테스트 클래스입니다.
 */
public class UserTest {

    /**
     * User 엔티티 객체 생성 및 필드 설정 테스트.
     */
    @Test
    void testUserEntityCreation() {
        // Given
        String email = "test@example.com";
        String name = "Test User";
        ProviderType provider = ProviderType.GOOGLE;
        String providerId = "1234567890";
        RoleType role = RoleType.USER;

        // When
        User user = User.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .build();

        // Then
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(provider, user.getProvider());
        assertEquals(providerId, user.getProviderId());
        assertEquals(role, user.getRole());
    }
}