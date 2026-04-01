package com.ims.api.security;

import com.ims.domain.port.UserRepositoryPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    private final UserRepositoryPort userRepository;

    public CurrentUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email))
                .getId();
    }
}
