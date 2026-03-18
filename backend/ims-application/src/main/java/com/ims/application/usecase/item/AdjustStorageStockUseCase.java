package com.ims.application.usecase.item;

import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.application.port.inbound.ItemCommandPort;
import com.ims.domain.event.ItemStockAdjustedEvent;
import com.ims.domain.exception.ItemNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdjustStorageStockUseCase implements ItemCommandPort {

    private final ItemRepositoryPort itemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final DomainEventPublisherPort eventPublisher;

    public AdjustStorageStockUseCase(ItemRepositoryPort itemRepository,
                                      TransactionRepositoryPort transactionRepository,
                                      DomainEventPublisherPort eventPublisher) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Item adjustStorageStock(AdjustStorageStockCommand command) {
        Item item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ItemNotFoundException(command.itemId()));

        int stockBefore = item.getTotalStorageStock().quantity();
        item.adjustStock(command.delta());
        int stockAfter = item.getTotalStorageStock().quantity();

        Item saved = itemRepository.save(item);

        transactionRepository.save(Transaction.create(
            null, item.getId(), TransactionType.STOCK_ADJUSTMENT,
            command.delta(), stockBefore, stockAfter, command.note(), command.createdBy()
        ));

        eventPublisher.publish(new ItemStockAdjustedEvent(saved.getId(), command.delta(), stockAfter));
        return saved;
    }

    @Override
    public Item registerItem(RegisterItemCommand command) {
        throw new UnsupportedOperationException("Handled by RegisterItemUseCase");
    }

    @Override
    public Item updateItem(UpdateItemCommand command) {
        throw new UnsupportedOperationException("Handled by UpdateItemUseCase");
    }
}
