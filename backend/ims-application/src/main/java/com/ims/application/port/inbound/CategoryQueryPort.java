package com.ims.application.port.inbound;

import com.ims.domain.model.Category;

import java.util.List;

public interface CategoryQueryPort {
    List<Category> listCategories();
}
