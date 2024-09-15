package com.neon.tonari.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 설정 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/css/**", "/js/**").permitAll()  // 특정 경로 허용
                        .anyRequest().authenticated()  // 그 외의 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // 사용자 정의 로그인 페이지 설정
                        .defaultSuccessUrl("/", true)  // 로그인 성공 후 리디렉션
                        .failureUrl("/login?error=true")
                );

        return http.build();
    }
}