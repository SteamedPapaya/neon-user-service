package com.neon.tonari.service;

import com.neon.tonari.entity.ProviderType;
import com.neon.tonari.entity.RoleType;
import com.neon.tonari.entity.User;
import com.neon.tonari.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = oAuth2User.getAttribute("sub"); // 예: Google의 사용자 ID 속성
        ProviderType provider = ProviderType.of(userRequest.getClientRegistration().getRegistrationId());
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .provider(provider)
                        .name(name)
                        .providerId(providerId)
                        .password("") // 소셜 로그인 사용자이기 때문에 비밀번호는 설정하지 않습니다.
                        .role(RoleType.USER) // 기본 사용자 권한 설정
                        .build());

        userRepository.save(user);

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())),
                oAuth2User.getAttributes(),
                "email");
    }
}