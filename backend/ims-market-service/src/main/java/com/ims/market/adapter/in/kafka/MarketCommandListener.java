package com.ims.market.adapter.in.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.market.domain.port.in.MarketStockUseCase;
import com.ims.market.domain.port.in.MarketUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class MarketCommandListener {

    private static final Logger log = LoggerFactory.getLogger(MarketCommandListener.class);

    private final MarketUseCase marketUseCase;
    private final MarketStockUseCase marketStockUseCase;

    public MarketCommandListener(MarketUseCase marketUseCase, MarketStockUseCase marketStockUseCase) {
        this.marketUseCase = marketUseCase;
        this.marketStockUseCase = marketStockUseCase;
    }

    @KafkaListener(topics = "ims.market.commands", groupId = "ims-market-service")
    public void handleCommand(EventEnvelope envelope) {
        if (envelope == null || envelope.getEventType() == null) return;

        log.info("Received market command: {}", envelope.getEventType());

        try {
            switch (envelope.getEventType()) {
                case "OPEN_MARKET_COMMAND" -> handleOpenMarket(envelope);
                case "CLOSE_MARKET_COMMAND" -> handleCloseMarket(envelope);
                case "STOCK_RECEIVE_REQUESTED" -> handleStockReceiveRequested(envelope);
                default -> log.warn("Unknown market command type: {}", envelope.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process market command {}: {}", envelope.getEventType(), e.getMessage(), e);
        }
    }

    private void handleOpenMarket(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID marketId = UUID.fromString((String) payload.get("marketId"));
        UUID requestingUserId = envelope.getUserId();
        marketUseCase.openMarket(marketId, requestingUserId != null ? requestingUserId : UUID.randomUUID());
        log.info("Opened market: {}", marketId);
    }

    private void handleCloseMarket(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID marketId = UUID.fromString((String) payload.get("marketId"));
        UUID requestingUserId = envelope.getUserId();
        marketUseCase.closeMarket(marketId, requestingUserId != null ? requestingUserId : UUID.randomUUID());
        log.info("Closed market: {}", marketId);
    }

    private void handleStockReceiveRequested(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID marketId = UUID.fromString((String) payload.get("marketId"));
        UUID itemId = UUID.fromString((String) payload.get("itemId"));
        int quantity = ((Number) payload.get("quantity")).intValue();
        UUID correlationId = envelope.getCorrelationId();
        marketStockUseCase.receiveStock(marketId, itemId, quantity, correlationId);
        log.info("Received {} units of item {} at market {}", quantity, itemId, marketId);
    }
}
