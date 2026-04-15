package com.ims.market.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.common.exception.ValidationException;
import com.ims.market.domain.model.Market;
import com.ims.market.domain.model.MarketStock;
import com.ims.market.domain.port.in.MarketStockUseCase;
import com.ims.market.domain.port.out.MarketEventPublisher;
import com.ims.market.domain.port.out.MarketRepository;
import com.ims.market.domain.port.out.MarketStockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MarketStockService implements MarketStockUseCase {

    private final MarketStockRepository stockRepository;
    private final MarketRepository marketRepository;
    private final MarketEventPublisher eventPublisher;

    public MarketStockService(MarketStockRepository stockRepository,
                               MarketRepository marketRepository,
                               MarketEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.marketRepository = marketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public MarketStock incrementStock(UUID marketId, UUID itemId, int quantity) {
        validateMarketIsOpen(marketId);

        MarketStock stock = findOrCreateStock(marketId, itemId);
        stock.increment(quantity);
        MarketStock saved = stockRepository.save(stock);

        publishStockEvent("MARKET_STOCK_INCREMENTED", marketId, itemId, quantity);
        return saved;
    }

    @Override
    public MarketStock decrementStock(UUID marketId, UUID itemId, int quantity) {
        validateMarketIsOpen(marketId);

        MarketStock stock = getStockOrThrow(marketId, itemId);
        stock.decrement(quantity);
        MarketStock saved = stockRepository.save(stock);

        publishStockEvent("MARKET_STOCK_DECREMENTED", marketId, itemId, quantity);
        return saved;
    }

    @Override
    public MarketStock setupAdjust(UUID marketId, UUID itemId, int delta) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));
        if (market.getStatus() != com.ims.market.domain.model.MarketStatus.SCHEDULED) {
            throw new ValidationException("Setup adjustments are only allowed while market is SCHEDULED");
        }
        if (delta == 0) throw new ValidationException("Delta must be non-zero");

        MarketStock stock = findOrCreateStock(marketId, itemId);
        if (delta > 0) {
            stock.increment(delta);
        } else {
            stock.decrement(Math.abs(delta));
        }
        return stockRepository.save(stock);
    }

    @Override
    public MarketStock receiveStock(UUID marketId, UUID itemId, int quantity, UUID correlationId) {
        // receiveStock is from transfer — market does not have to be OPEN
        MarketStock stock = findOrCreateStock(marketId, itemId);
        stock.increment(quantity);
        MarketStock saved = stockRepository.save(stock);

        publishStockEvent("MARKET_STOCK_RECEIVED_CONFIRMED", marketId, itemId, quantity, correlationId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public MarketStock getStock(UUID marketId, UUID itemId) {
        return getStockOrThrow(marketId, itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MarketStock> listStockByMarket(UUID marketId, int page, int size) {
        Page<MarketStock> result = stockRepository.findByMarketId(marketId, PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    private void validateMarketIsOpen(UUID marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));
        if (!market.isOpen()) {
            throw new ValidationException("Market is not open: " + marketId);
        }
    }

    private MarketStock findOrCreateStock(UUID marketId, UUID itemId) {
        return stockRepository.findByMarketIdAndItemId(marketId, itemId)
                .orElseGet(() -> MarketStock.builder()
                        .marketId(marketId)
                        .itemId(itemId)
                        .quantity(0)
                        .build());
    }

    private MarketStock getStockOrThrow(UUID marketId, UUID itemId) {
        return stockRepository.findByMarketIdAndItemId(marketId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MarketStock for market=" + marketId + " item=" + itemId));
    }

    private void publishStockEvent(String eventType, UUID marketId, UUID itemId, int quantity) {
        publishStockEvent(eventType, marketId, itemId, quantity, null);
    }

    private void publishStockEvent(String eventType, UUID marketId, UUID itemId, int quantity, UUID correlationId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("marketId", marketId.toString());
        payload.put("itemId", itemId.toString());
        payload.put("quantity", quantity);

        EventEnvelope event = EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(correlationId != null ? correlationId : UUID.randomUUID())
                .eventType(eventType)
                .version(1)
                .originService("ims-market-service")
                .occurredAt(java.time.Instant.now())
                .payload(payload)
                .build();
        eventPublisher.publish(event);
    }
}
