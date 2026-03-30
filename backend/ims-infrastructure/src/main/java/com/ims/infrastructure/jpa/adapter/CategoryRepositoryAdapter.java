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
        CategoryJpaEntity entity = toEntity(category);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
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
        e.setName(category.getName());
        e.setCreatedAt(category.getCreatedAt());
        return e;
    }

    private Category toDomain(CategoryJpaEntity e) {
        return new Category(e.getId(), e.getName(), e.getCreatedAt());
    }
}
