package com.ims.infrastructure.security;

import com.ims.domain.model.User;
import com.ims.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedUsers() {
        if (!userRepository.existsByEmail("admin@ims.com")) {
            User admin = User.create("admin@ims.com", passwordEncoder.encode("Admin123!"), "ADMIN");
            userRepository.save(admin);
            log.info("Test user created: admin@ims.com / Admin123!");
        }
    }
}
