package com.ims.infrastructure.security;

import com.ims.domain.model.User;
import com.ims.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(TestDataInitializer.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataInitializer(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedTestUsers() {
        createIfAbsent("test@ims.com",  "Test123!",  "USER");
        createIfAbsent("tester@ims.com", "Tester123!", "ADMIN");
    }

    private void createIfAbsent(String email, String password, String role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.create(email, passwordEncoder.encode(password), role));
            log.info("Test user created: {} / {} ({})", email, password, role);
        }
    }
}
