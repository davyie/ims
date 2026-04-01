package com.ims.api.controller;

import com.ims.api.dto.request.CreateCategoryRequest;
import com.ims.api.dto.response.CategoryResponse;
import com.ims.api.security.CurrentUserService;
import com.ims.application.command.CreateCategoryCommand;
import com.ims.application.port.inbound.CategoryCommandPort;
import com.ims.application.port.inbound.CategoryQueryPort;
import com.ims.domain.model.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;
    private final CurrentUserService currentUserService;

    public CategoryController(CategoryCommandPort categoryCommandPort, CategoryQueryPort categoryQueryPort,
            CurrentUserService currentUserService) {
        this.categoryCommandPort = categoryCommandPort;
        this.categoryQueryPort = categoryQueryPort;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        UUID userId = currentUserService.getCurrentUserId();
        List<CategoryResponse> categories = categoryQueryPort.listCategories(userId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Category category = categoryCommandPort.createCategory(new CreateCategoryCommand(userId, request.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryCommandPort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getCreatedAt());
    }
}
