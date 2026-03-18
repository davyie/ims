package com.ims.application.dto;

import java.util.UUID;

public record StorageItemDto(
    UUID itemId,
    String sku,
    String name,
    String category,
    int currentStock
) {}
