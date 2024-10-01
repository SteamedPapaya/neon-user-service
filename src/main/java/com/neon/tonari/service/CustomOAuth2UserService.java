package com.neon.tonari.service;

import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.RoleType;
import com.neon.tonari.entity.User;
import com.neon.tonari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // OAuth2 로그인 시 사용자 정보 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 추출 및 저장
        String providerId = oAuth2User.getAttribute("sub");
        ProviderType provider = ProviderType.of(userRequest.getClientRegistration().getRegistrationId());
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .provider(provider)
                        .name(name)
                        .providerId(providerId)
                        .role(RoleType.USER)
                        .build());

        userRepository.save(user);

        // Spring Security의 OAuth2User 객체로 반환
        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())),
                oAuth2User.getAttributes(),
                "email");
    }
}