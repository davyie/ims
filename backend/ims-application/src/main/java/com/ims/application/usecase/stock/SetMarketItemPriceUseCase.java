package com.ims.application.usecase.stock;

import com.ims.application.command.SetPriceCommand;
import com.ims.domain.exception.MarketItemNotFoundException;
import com.ims.domain.model.MarketItem;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.valueobject.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetMarketItemPriceUseCase {

    private final MarketItemRepositoryPort marketItemRepository;

    public SetMarketItemPriceUseCase(MarketItemRepositoryPort marketItemRepository) {
        this.marketItemRepository = marketItemRepository;
    }

    @Transactional
    public MarketItem setPrice(SetPriceCommand command) {
        MarketItem marketItem = marketItemRepository.findByMarketIdAndItemId(command.marketId(), command.itemId())
                .orElseThrow(() -> new MarketItemNotFoundException(command.marketId(), command.itemId()));

        marketItem.setMarketPrice(Money.of(command.price(), command.currency()));
        return marketItemRepository.save(marketItem);
    }
}
