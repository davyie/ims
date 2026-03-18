package com.ims.domain.exception;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("Item with SKU already exists: " + sku);
    }
}
