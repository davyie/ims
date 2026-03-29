package com.ims.application.usecase.item;

import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.application.port.inbound.ItemCommandPort;
import com.ims.domain.exception.ItemNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.domain.port.MarketItemRepositoryPort;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteItemUseCase implements ItemCommandPort {

    private final ItemRepositoryPort itemRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final MarketItemRepositoryPort marketItemRepository;

    public DeleteItemUseCase(ItemRepositoryPort itemRepository,
                             TransactionRepositoryPort transactionRepository,
                             MarketItemRepositoryPort marketItemRepository) {
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
        this.marketItemRepository = marketItemRepository;
    }

    @Override
    @Transactional
    public void deleteItem(UUID id) {
        itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        // Remove child records first to satisfy FK constraints
        transactionRepository.deleteByItemId(id);
        marketItemRepository.deleteByItemId(id);
        itemRepository.deleteById(id);
    }

    @Override
    public Item registerItem(RegisterItemCommand command) {
        throw new UnsupportedOperationException("Handled by RegisterItemUseCase");
    }

    @Override
    public Item updateItem(UpdateItemCommand command) {
        throw new UnsupportedOperationException("Handled by UpdateItemUseCase");
    }

    @Override
    public Item adjustStorageStock(AdjustStorageStockCommand command) {
        throw new UnsupportedOperationException("Handled by AdjustStorageStockUseCase");
    }
}
