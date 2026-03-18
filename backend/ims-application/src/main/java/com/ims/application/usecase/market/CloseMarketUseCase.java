package com.ims.application.usecase.market;

import com.ims.application.command.CloseMarketCommand;
import com.ims.domain.event.MarketClosedEvent;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Market;
import com.ims.domain.model.MarketItem;
import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CloseMarketUseCase {

    private final MarketRepositoryPort marketRepository;
    private final MarketItemRepositoryPort marketItemRepository;
    private final ItemRepositoryPort itemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final DomainEventPublisherPort eventPublisher;

    public CloseMarketUseCase(MarketRepositoryPort marketRepository,
                               MarketItemRepositoryPort marketItemRepository,
                               ItemRepositoryPort itemRepository,
                               TransactionRepositoryPort transactionRepository,
                               DomainEventPublisherPort eventPublisher) {
        this.marketRepository = marketRepository;
        this.marketItemRepository = marketItemRepository;
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Market closeMarket(CloseMarketCommand command) {
        Market market = marketRepository.findById(command.marketId())
                .orElseThrow(() -> new MarketNotFoundException(command.marketId()));

        market.close();

        // Return remaining market stock to storage
        List<MarketItem> marketItems = marketItemRepository.findAllByMarketId(market.getId());
        for (MarketItem mi : marketItems) {
            int remaining = mi.getCurrentStock().quantity();
            if (remaining > 0) {
                itemRepository.findById(mi.getItemId()).ifPresent(item -> {
                    int stockBefore = item.getTotalStorageStock().quantity();
                    item.adjustStock(remaining);
                    itemRepository.save(item);
                    transactionRepository.save(Transaction.create(
                        market.getId(), item.getId(), TransactionType.RETURN_FROM_MARKET,
                        remaining, stockBefore, item.getTotalStorageStock().quantity(),
                        "Stock returned on market close", command.createdBy()
                    ));
                });
            }
        }

        Market saved = marketRepository.save(market);
        eventPublisher.publish(new MarketClosedEvent(saved.getId(), saved.getName()));
        return saved;
    }
}
