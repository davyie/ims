package com.ims.domain.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(UUID id) {
        super("Item not found with id: " + id);
    }
    public ItemNotFoundException(String sku) {
        super("Item not found with SKU: " + sku);
    }
}
