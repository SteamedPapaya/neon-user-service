package com.neon.tonari.service;

import com.neon.tonari.entity.User;
import com.neon.tonari.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow( () -> new EntityNotFoundException( "User with email " + email + " not found" ) )
                ;
    }
}
