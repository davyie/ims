package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new category")
public record CreateCategoryRequest(
    @NotBlank @Schema(description = "Category name", example = "Furniture") String name
) {}
