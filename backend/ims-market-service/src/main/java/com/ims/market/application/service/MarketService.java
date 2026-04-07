package com.ims.market.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.market.domain.model.Market;
import com.ims.market.domain.model.MarketStatus;
import com.ims.market.domain.model.MarketType;
import com.ims.market.domain.port.in.MarketUseCase;
import com.ims.market.domain.port.out.MarketEventPublisher;
import com.ims.market.domain.port.out.MarketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MarketService implements MarketUseCase {

    private final MarketRepository marketRepository;
    private final MarketEventPublisher eventPublisher;

    public MarketService(MarketRepository marketRepository, MarketEventPublisher eventPublisher) {
        this.marketRepository = marketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Market createMarket(String name, String location, MarketType marketType, String description, UUID userId) {
        Market market = Market.builder()
                .name(name)
                .location(location)
                .marketType(marketType)
                .description(description)
                .userId(userId)
                .status(MarketStatus.SCHEDULED)
                .build();

        Market saved = marketRepository.save(market);

        Map<String, Object> payload = new HashMap<>();
        payload.put("marketId", saved.getMarketId().toString());
        payload.put("name", saved.getName());
        payload.put("userId", userId.toString());

        eventPublisher.publish(EventEnvelope.of("MARKET_CREATED", "ims-market-service", userId, payload));
        return saved;
    }

    @Override
    public Market updateMarket(UUID marketId, String name, String location, String description, UUID requestingUserId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));

        if (name != null) market.setName(name);
        if (location != null) market.setLocation(location);
        if (description != null) market.setDescription(description);

        Market saved = marketRepository.save(market);
        eventPublisher.publish(EventEnvelope.of("MARKET_UPDATED", "ims-market-service", requestingUserId,
                Map.of("marketId", marketId.toString())));
        return saved;
    }

    @Override
    public void deleteMarket(UUID marketId, UUID requestingUserId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));

        market.setStatus(MarketStatus.ARCHIVED);
        marketRepository.save(market);

        eventPublisher.publish(EventEnvelope.of("MARKET_DELETED", "ims-market-service", requestingUserId,
                Map.of("marketId", marketId.toString())));
    }

    @Override
    @Transactional(readOnly = true)
    public Market getMarketById(UUID marketId) {
        return marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Market> listMarkets(UUID userId, int page, int size) {
        Page<Market> result = userId != null
                ? marketRepository.findByUserId(userId, PageRequest.of(page, size))
                : marketRepository.findAll(PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    @Override
    public Market openMarket(UUID marketId, UUID requestingUserId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));

        market.open();
        Market saved = marketRepository.save(market);

        eventPublisher.publish(EventEnvelope.of("MARKET_OPENED", "ims-market-service", requestingUserId,
                Map.of("marketId", marketId.toString())));
        return saved;
    }

    @Override
    public Market closeMarket(UUID marketId, UUID requestingUserId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", marketId));

        market.close();
        Market saved = marketRepository.save(market);

        eventPublisher.publish(EventEnvelope.of("MARKET_CLOSED", "ims-market-service", requestingUserId,
                Map.of("marketId", marketId.toString())));
        return saved;
    }
}
