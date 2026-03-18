package com.ims.application.usecase.item;

import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.application.port.inbound.ItemCommandPort;
import com.ims.domain.exception.ItemNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StoragePosition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateItemUseCase implements ItemCommandPort {

    private final ItemRepositoryPort itemRepository;

    public UpdateItemUseCase(ItemRepositoryPort itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Item updateItem(UpdateItemCommand command) {
        Item item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ItemNotFoundException(command.itemId()));

        Money price = Money.of(command.defaultPrice(), command.currency());
        StoragePosition position = new StoragePosition(command.zone(), command.shelf(), command.row(), command.column());
        item.update(command.name(), command.description(), command.category(), price, position);

        return itemRepository.save(item);
    }

    @Override
    public Item registerItem(RegisterItemCommand command) {
        throw new UnsupportedOperationException("Handled by RegisterItemUseCase");
    }

    @Override
    public Item adjustStorageStock(AdjustStorageStockCommand command) {
        throw new UnsupportedOperationException("Handled by AdjustStorageStockUseCase");
    }
}
