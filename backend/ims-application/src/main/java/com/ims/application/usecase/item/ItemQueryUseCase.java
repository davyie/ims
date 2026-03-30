package com.ims.application.usecase.item;

import com.ims.application.port.inbound.ItemQueryPort;
import com.ims.application.query.GetItemQuery;
import com.ims.application.query.ListItemsQuery;
import com.ims.domain.exception.ItemNotFoundException;
import com.ims.domain.model.Item;
import com.ims.domain.port.ItemRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemQueryUseCase implements ItemQueryPort {

    private final ItemRepositoryPort itemRepository;

    public ItemQueryUseCase(ItemRepositoryPort itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item getItem(GetItemQuery query) {
        return itemRepository.findById(query.itemId())
                .orElseThrow(() -> new ItemNotFoundException(query.itemId()));
    }

    @Override
    public List<Item> listItems(ListItemsQuery query) {
        List<Item> all = itemRepository.findAll();
        if (query.category() != null && !query.category().isBlank()) {
            return all.stream()
                    .filter(i -> query.category().equalsIgnoreCase(i.getCategory()))
                    .collect(Collectors.toList());
        }
        return all;
    }
}
