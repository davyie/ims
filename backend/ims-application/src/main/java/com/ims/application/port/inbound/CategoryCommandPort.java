package com.ims.application.port.inbound;

import com.ims.application.command.CreateCategoryCommand;
import com.ims.domain.model.Category;

import java.util.UUID;

public interface CategoryCommandPort {
    Category createCategory(CreateCategoryCommand command);
    void deleteCategory(UUID id);
}
