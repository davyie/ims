package com.ims.domain.port;

import java.util.UUID;

public interface StorageStockPort {
    void addStock(UUID itemId, int quantity);
    void deductStock(UUID itemId, int quantity);
    int getStock(UUID itemId);
}
