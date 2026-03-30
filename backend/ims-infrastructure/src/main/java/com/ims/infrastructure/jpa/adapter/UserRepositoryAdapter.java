package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.User;
import com.ims.domain.port.UserRepositoryPort;
import com.ims.infrastructure.jpa.entity.UserJpaEntity;
import com.ims.infrastructure.jpa.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return toDomain(jpaRepository.save(toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private UserJpaEntity toEntity(User user) {
        UserJpaEntity e = new UserJpaEntity();
        e.setId(user.getId());
        e.setEmail(user.getEmail());
        e.setPassword(user.getPassword());
        e.setRole(user.getRole());
        e.setCreatedAt(user.getCreatedAt());
        return e;
    }

    private User toDomain(UserJpaEntity e) {
        return new User(e.getId(), e.getEmail(), e.getPassword(), e.getRole(), e.getCreatedAt());
    }
}
