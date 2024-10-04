package com.neon.tonari.config;

import com.neon.tonari.security.jwt.JwtAuthenticationFilter;
import com.neon.tonari.security.jwt.JwtTokenProvider;
import com.neon.tonari.service.CustomOAuth2UserService;
import com.neon.tonari.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.client.origin}")
    private String clientOrigin;

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;  // OAuth2 인증 서비스 추가

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())

                // 경로별 인증 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/css/**", "/js/**", "/api/auth/token", "/oauth2/**", "/login/oauth2/code/**").permitAll()  // 인증 불필요 경로
                        .requestMatchers("/**").authenticated()  // JWT 인증이 필요한 경로
                        .anyRequest().authenticated()  // 그 외 요청은 인증 필요
                )

                // 폼 기반 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))  // OAuth2 유저 서비스 연결
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            String token = jwtTokenProvider.generateToken(authentication.getName());
                            response.sendRedirect(clientOrigin + "/login?token=" + token);  // JWT 생성 후 리디렉션
                        })
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가, JWT 검증은 /api 경로에서만 수행하도록 필터 설정
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                                response.setStatus(HttpServletResponse.SC_OK);  // OPTIONS 요청은 OK 반환
                            } else {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 에러 반환
                                response.getWriter().write("Unauthorized");
                            }
                        })
                );

        return http.build();
    }

    // JWT 필터 빈 생성
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://neon7.site", "https://api.neon7.site"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // AuthenticationManager 빈 생성
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}