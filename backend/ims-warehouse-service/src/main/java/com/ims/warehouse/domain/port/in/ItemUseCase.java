package com.ims.warehouse.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.warehouse.domain.model.Item;

import java.math.BigDecimal;
import java.util.UUID;

public interface ItemUseCase {

    Item createItem(String sku, String name, String description, String category,
                    String unitOfMeasure, BigDecimal unitPrice, UUID userId);

    Item updateItem(UUID itemId, String name, String description, String category,
                    String unitOfMeasure, BigDecimal unitPrice, UUID requestingUserId);

    void deleteItem(UUID itemId, UUID requestingUserId);

    Item getItemById(UUID itemId);

    PageResponse<Item> listItems(UUID userId, int page, int size);
}
