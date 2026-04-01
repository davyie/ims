package com.ims.application.port.inbound;

import com.ims.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryQueryPort {
    List<Category> listCategories(UUID userId);
}
