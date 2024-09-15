package com.neon.tonari.repository;

import com.neon.tonari.entity.User;
import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository의 동작을 검증하기 위한 테스트 클래스입니다.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    /**
     * 이메일로 사용자 조회 테스트.
     */
    @Test
    void testFindByEmail() {
        // Given
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .provider(ProviderType.GOOGLE)
                .providerId("1234567890")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }
}