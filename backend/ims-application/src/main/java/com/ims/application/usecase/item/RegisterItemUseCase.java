package com.ims.application.usecase.item;

import com.ims.application.command.RegisterItemCommand;
import com.ims.application.port.inbound.ItemCommandPort;
import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.domain.event.ItemCreatedEvent;
import com.ims.domain.exception.DuplicateSkuException;
import com.ims.domain.model.Item;
import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StoragePosition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterItemUseCase implements ItemCommandPort {

    private final ItemRepositoryPort itemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final DomainEventPublisherPort eventPublisher;

    public RegisterItemUseCase(ItemRepositoryPort itemRepository,
                               TransactionRepositoryPort transactionRepository,
                               DomainEventPublisherPort eventPublisher) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Item registerItem(RegisterItemCommand command) {
        itemRepository.findBySku(command.sku()).ifPresent(existing -> {
            throw new DuplicateSkuException(command.sku());
        });

        Money price = Money.of(command.defaultPrice(), command.currency());
        StoragePosition position = new StoragePosition(command.zone(), command.shelf(), command.row(), command.column());
        Item item = Item.create(command.sku(), command.name(), command.description(),
                command.category(), price, position);

        if (command.initialStock() > 0) {
            item.adjustStock(command.initialStock());
        }

        Item saved = itemRepository.save(item);

        if (command.initialStock() > 0) {
            transactionRepository.save(Transaction.create(
                null, saved.getId(), TransactionType.STOCK_ADJUSTMENT,
                command.initialStock(), 0, command.initialStock(),
                "Initial stock on registration", "system"
            ));
        }

        eventPublisher.publish(new ItemCreatedEvent(saved.getId(), saved.getSku(), saved.getName()));
        return saved;
    }

    @Override
    @Transactional
    public Item updateItem(UpdateItemCommand command) {
        throw new UnsupportedOperationException("Handled by UpdateItemUseCase");
    }

    @Override
    @Transactional
    public Item adjustStorageStock(AdjustStorageStockCommand command) {
        throw new UnsupportedOperationException("Handled by AdjustStorageStockUseCase");
    }

    @Override
    public void deleteItem(java.util.UUID id) {
        throw new UnsupportedOperationException("Handled by DeleteItemUseCase");
    }
}
