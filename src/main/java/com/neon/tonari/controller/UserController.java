package com.neon.tonari.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {  // JSON 객체 반환
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            response.put("email", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities());
            // 필요한 경우 추가 필드를 더 넣을 수 있습니다.
        } else {
            response.put("error", "Unauthorized");
        }
        return response;
    }
}