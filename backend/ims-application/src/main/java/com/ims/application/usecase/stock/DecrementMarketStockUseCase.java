package com.ims.application.usecase.stock;

import com.ims.application.command.DecrementStockCommand;
import com.ims.domain.event.MarketStockDecrementedEvent;
import com.ims.domain.exception.MarketItemNotFoundException;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.MarketItem;
import com.ims.domain.model.Transaction;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecrementMarketStockUseCase {

    private final MarketRepositoryPort marketRepository;
    private final MarketItemRepositoryPort marketItemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final DomainEventPublisherPort eventPublisher;

    public DecrementMarketStockUseCase(MarketRepositoryPort marketRepository,
                                        MarketItemRepositoryPort marketItemRepository,
                                        TransactionRepositoryPort transactionRepository,
                                        DomainEventPublisherPort eventPublisher) {
        this.marketRepository = marketRepository;
        this.marketItemRepository = marketItemRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public MarketItem decrementStock(DecrementStockCommand command) {
        marketRepository.findById(command.marketId())
                .orElseThrow(() -> new MarketNotFoundException(command.marketId()));

        MarketItem marketItem = marketItemRepository.findByMarketIdAndItemId(command.marketId(), command.itemId())
                .orElseThrow(() -> new MarketItemNotFoundException(command.marketId(), command.itemId()));

        int stockBefore = marketItem.getCurrentStock().quantity();
        marketItem.decrement(command.quantity());
        MarketItem saved = marketItemRepository.save(marketItem);

        transactionRepository.save(Transaction.createSale(
            command.userId(), command.marketId(), command.itemId(),
            -command.quantity(), stockBefore, saved.getCurrentStock().quantity(),
            command.note(), command.createdBy(),
            command.salePrice(), command.saleCurrency()
        ));

        eventPublisher.publish(new MarketStockDecrementedEvent(command.marketId(), command.itemId(), command.quantity()));
        return saved;
    }
}
