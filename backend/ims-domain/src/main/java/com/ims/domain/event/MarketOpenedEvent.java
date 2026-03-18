package com.ims.domain.event;

import java.util.UUID;

public class MarketOpenedEvent extends AbstractDomainEvent {
    private final String marketName;

    public MarketOpenedEvent(UUID marketId, String marketName) {
        super("MarketOpened", marketId);
        this.marketName = marketName;
    }

    public String getMarketName() { return marketName; }
}
