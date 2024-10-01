package com.neon.tonari.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 정보를 나타내는 엔티티 클래스입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    /**
     * 사용자 고유 ID입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 이메일 주소입니다.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 사용자 계정의 비밀번호입니다.
     */
    @Column(nullable = false)
    private String password;

    /**
     * 소셜 로그인 제공자입니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ProviderType provider;

    /**
     * 사용자의 이름입니다.
     */
    @Column(nullable = false)
    private String name;

    /**
     * 제공자에서 발급한 사용자 ID입니다.
     */
    @Column(nullable = true)
    private String providerId;

    /**
     * 사용자의 권한 유형입니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    public User(Long id, String email, String password, ProviderType provider, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.name = name;
    }
}