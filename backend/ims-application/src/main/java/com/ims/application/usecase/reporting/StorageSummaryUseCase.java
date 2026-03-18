package com.ims.application.usecase.reporting;

import com.ims.application.dto.StorageItemDto;
import com.ims.application.dto.StorageSummaryDto;
import com.ims.application.port.inbound.StorageSummaryPort;
import com.ims.application.query.GetStorageSummaryQuery;
import com.ims.domain.model.Item;
import com.ims.domain.port.ItemRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StorageSummaryUseCase implements StorageSummaryPort {

    private final ItemRepositoryPort itemRepository;

    public StorageSummaryUseCase(ItemRepositoryPort itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public StorageSummaryDto getStorageSummary(GetStorageSummaryQuery query) {
        List<StorageItemDto> items = itemRepository.findAll().stream()
                .map(item -> new StorageItemDto(
                    item.getId(), item.getSku(), item.getName(),
                    item.getCategory(), item.getTotalStorageStock().quantity()
                ))
                .toList();
        return new StorageSummaryDto(items);
    }
}
