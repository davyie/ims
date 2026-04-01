package com.ims.domain.port;

import com.ims.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    List<Category> findAllByUserId(UUID userId);
    Optional<Category> findByNameAndUserId(String name, UUID userId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
