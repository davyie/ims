package com.ims.application.usecase.category;

import com.ims.application.command.CreateCategoryCommand;
import com.ims.application.port.inbound.CategoryCommandPort;
import com.ims.application.port.inbound.CategoryQueryPort;
import com.ims.domain.model.Category;
import com.ims.domain.port.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryUseCase implements CategoryCommandPort, CategoryQueryPort {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryUseCase(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(CreateCategoryCommand command) {
        categoryRepository.findByNameAndUserId(command.name(), command.userId()).ifPresent(existing -> {
            throw new IllegalArgumentException("Category already exists: " + command.name());
        });
        return categoryRepository.save(Category.create(command.userId(), command.name()));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listCategories(UUID userId) {
        return categoryRepository.findAllByUserId(userId);
    }
}
