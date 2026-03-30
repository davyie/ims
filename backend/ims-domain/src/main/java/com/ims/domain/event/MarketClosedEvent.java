package com.ims.domain.event;

import java.util.UUID;

public class MarketClosedEvent extends AbstractDomainEvent {
    private final String marketName;

    public MarketClosedEvent(UUID marketId, String marketName) {
        super("MarketClosed", marketId);
        this.marketName = marketName;
    }

    public String getMarketName() { return marketName; }
}
