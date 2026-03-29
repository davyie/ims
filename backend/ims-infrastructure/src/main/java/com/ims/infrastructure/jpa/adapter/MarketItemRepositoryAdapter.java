package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.MarketItem;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StockLevel;
import com.ims.infrastructure.jpa.entity.MarketItemJpaEntity;
import com.ims.infrastructure.jpa.repository.MarketItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MarketItemRepositoryAdapter implements MarketItemRepositoryPort {

    private final MarketItemJpaRepository jpaRepository;

    public MarketItemRepositoryAdapter(MarketItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MarketItem save(MarketItem mi) {
        return toDomain(jpaRepository.save(toEntity(mi)));
    }

    @Override
    public Optional<MarketItem> findByMarketIdAndItemId(UUID marketId, UUID itemId) {
        return jpaRepository.findByMarketIdAndItemId(marketId, itemId).map(this::toDomain);
    }

    @Override
    public List<MarketItem> findAllByMarketId(UUID marketId) {
        return jpaRepository.findAllByMarketId(marketId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByItemId(UUID itemId) {
        jpaRepository.deleteByItemId(itemId);
    }

    private MarketItemJpaEntity toEntity(MarketItem mi) {
        MarketItemJpaEntity e = new MarketItemJpaEntity();
        e.setId(mi.getId());
        e.setMarketId(mi.getMarketId());
        e.setItemId(mi.getItemId());
        e.setAllocatedStock(mi.getAllocatedStock().quantity());
        e.setCurrentStock(mi.getCurrentStock().quantity());
        if (mi.getMarketPrice() != null) {
            e.setMarketPrice(mi.getMarketPrice().amount());
            e.setMarketCurrency(mi.getMarketPrice().currency());
        }
        return e;
    }

    private MarketItem toDomain(MarketItemJpaEntity e) {
        Money price = (e.getMarketPrice() != null && e.getMarketCurrency() != null)
            ? Money.of(e.getMarketPrice(), e.getMarketCurrency()) : null;
        return new MarketItem(e.getId(), e.getMarketId(), e.getItemId(),
            StockLevel.of(e.getAllocatedStock()), StockLevel.of(e.getCurrentStock()), price);
    }
}
