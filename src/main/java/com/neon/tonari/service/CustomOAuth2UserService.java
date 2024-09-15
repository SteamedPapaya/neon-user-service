package com.neon.tonari.service;

import com.neon.tonari.entity.ProviderType;
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

        ProviderType provider = ProviderType.of(userRequest.getClientRegistration().getRegistrationId());
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseGet(() -> new User(null, email, "", provider, oAuth2User.getAttribute("name")));
        userRepository.save(user);

        return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email");
    }
}