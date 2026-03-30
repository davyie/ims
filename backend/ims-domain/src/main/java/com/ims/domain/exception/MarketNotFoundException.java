package com.ims.domain.exception;

import java.util.UUID;

public class MarketNotFoundException extends RuntimeException {
    public MarketNotFoundException(UUID id) {
        super("Market not found with id: " + id);
    }
}
