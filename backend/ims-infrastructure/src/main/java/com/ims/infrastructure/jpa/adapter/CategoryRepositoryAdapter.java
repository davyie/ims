package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.Category;
import com.ims.domain.port.CategoryRepositoryPort;
import com.ims.infrastructure.jpa.entity.CategoryJpaEntity;
import com.ims.infrastructure.jpa.repository.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryAdapter(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Category save(Category category) {
        return toDomain(jpaRepository.save(toEntity(category)));
    }

    @Override
    public List<Category> findAllByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByNameAndUserId(String name, UUID userId) {
        return jpaRepository.findByNameAndUserId(name, userId).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private CategoryJpaEntity toEntity(Category category) {
        CategoryJpaEntity e = new CategoryJpaEntity();
        e.setId(category.getId());
        e.setUserId(category.getUserId());
        e.setName(category.getName());
        e.setCreatedAt(category.getCreatedAt());
        return e;
    }

    private Category toDomain(CategoryJpaEntity e) {
        return new Category(e.getId(), e.getUserId(), e.getName(), e.getCreatedAt());
    }
}
