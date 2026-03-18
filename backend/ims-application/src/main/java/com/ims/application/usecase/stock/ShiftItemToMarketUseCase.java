package com.ims.application.usecase.stock;

import com.ims.application.command.DecrementStockCommand;
import com.ims.application.command.IncrementStockCommand;
import com.ims.application.command.SetPriceCommand;
import com.ims.application.command.ShiftItemCommand;
import com.ims.application.port.inbound.MarketStockCommandPort;
import com.ims.domain.event.ItemShiftedToMarketEvent;
import com.ims.domain.exception.InsufficientStockException;
import com.ims.domain.exception.InvalidMarketStateException;
import com.ims.domain.exception.ItemNotFoundException;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.model.Market;
import com.ims.domain.model.MarketItem;
import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import com.ims.domain.valueobject.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShiftItemToMarketUseCase implements MarketStockCommandPort {

    private final MarketRepositoryPort marketRepository;
    private final ItemRepositoryPort itemRepository;
    private final MarketItemRepositoryPort marketItemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final IncrementMarketStockUseCase incrementUseCase;
    private final DecrementMarketStockUseCase decrementUseCase;
    private final SetMarketItemPriceUseCase setPriceUseCase;

    public ShiftItemToMarketUseCase(MarketRepositoryPort marketRepository,
                                     ItemRepositoryPort itemRepository,
                                     MarketItemRepositoryPort marketItemRepository,
                                     TransactionRepositoryPort transactionRepository,
                                     DomainEventPublisherPort eventPublisher,
                                     IncrementMarketStockUseCase incrementUseCase,
                                     DecrementMarketStockUseCase decrementUseCase,
                                     SetMarketItemPriceUseCase setPriceUseCase) {
        this.marketRepository = marketRepository;
        this.itemRepository = itemRepository;
        this.marketItemRepository = marketItemRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.incrementUseCase = incrementUseCase;
        this.decrementUseCase = decrementUseCase;
        this.setPriceUseCase = setPriceUseCase;
    }

    @Override
    @Transactional
    public MarketItem shiftItem(ShiftItemCommand command) {
        Market market = marketRepository.findById(command.marketId())
                .orElseThrow(() -> new MarketNotFoundException(command.marketId()));

        if (!market.isOpen()) {
            throw new InvalidMarketStateException("Can only shift items to OPEN markets, current status: " + market.getStatus());
        }

        Item item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ItemNotFoundException(command.itemId()));

        int availableStock = item.getTotalStorageStock().quantity();
        if (availableStock < command.quantity()) {
            throw new InsufficientStockException("Insufficient storage stock. Available: " + availableStock + ", requested: " + command.quantity());
        }

        int stockBefore = availableStock;
        item.adjustStock(-command.quantity());
        itemRepository.save(item);

        Money marketPrice = Money.of(command.marketPrice(), command.currency());
        Optional<MarketItem> existing = marketItemRepository.findByMarketIdAndItemId(command.marketId(), command.itemId());
        MarketItem marketItem;
        if (existing.isPresent()) {
            marketItem = existing.get();
            marketItem.addStock(command.quantity());
        } else {
            marketItem = MarketItem.create(command.marketId(), command.itemId(), command.quantity(), marketPrice);
        }
        MarketItem saved = marketItemRepository.save(marketItem);

        transactionRepository.save(Transaction.create(
            command.marketId(), command.itemId(), TransactionType.SHIFT_TO_MARKET,
            -command.quantity(), stockBefore, item.getTotalStorageStock().quantity(),
            "Shifted to market", command.createdBy()
        ));

        eventPublisher.publish(new ItemShiftedToMarketEvent(command.marketId(), command.itemId(), command.quantity()));
        return saved;
    }

    @Override
    public MarketItem incrementStock(IncrementStockCommand command) {
        return incrementUseCase.incrementStock(command);
    }

    @Override
    public MarketItem decrementStock(DecrementStockCommand command) {
        return decrementUseCase.decrementStock(command);
    }

    @Override
    public MarketItem setPrice(SetPriceCommand command) {
        return setPriceUseCase.setPrice(command);
    }
}
