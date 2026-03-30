package com.ims.domain.exception;

import java.util.UUID;

public class MarketItemNotFoundException extends RuntimeException {
    public MarketItemNotFoundException(UUID marketId, UUID itemId) {
        super("Market item not found for market " + marketId + " and item " + itemId);
    }
}
