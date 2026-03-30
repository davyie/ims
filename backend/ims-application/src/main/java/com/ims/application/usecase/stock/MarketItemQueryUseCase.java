package com.ims.application.usecase.stock;

import com.ims.application.port.inbound.MarketItemQueryPort;
import com.ims.domain.model.MarketItem;
import com.ims.domain.port.MarketItemRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MarketItemQueryUseCase implements MarketItemQueryPort {

    private final MarketItemRepositoryPort marketItemRepository;

    public MarketItemQueryUseCase(MarketItemRepositoryPort marketItemRepository) {
        this.marketItemRepository = marketItemRepository;
    }

    @Override
    public List<MarketItem> getMarketItems(UUID marketId) {
        return marketItemRepository.findAllByMarketId(marketId);
    }
}
