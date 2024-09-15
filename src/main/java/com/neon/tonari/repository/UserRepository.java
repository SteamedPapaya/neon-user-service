package com.neon.tonari.repository;

import com.neon.tonari.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 사용자 정보를 관리하기 위한 리포지토리 인터페이스입니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일을 기반으로 사용자를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일
     * @return 이메일에 해당하는 사용자(Optional)
     */
    Optional<User> findByEmail(String email);
}