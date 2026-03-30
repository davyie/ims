package com.ims.api.dto.response;

import java.util.UUID;

public record StorageItemResponse(
    UUID itemId,
    String sku,
    String name,
    String category,
    int currentStock
) {}
