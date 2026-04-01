package com.ims.domain.port;

import com.ims.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    List<Transaction> findAllByUserId(UUID userId);
    List<Transaction> findByMarketId(UUID marketId);
    List<Transaction> findByItemId(UUID itemId);
    List<Transaction> findByMarketIdAndItemId(UUID marketId, UUID itemId);
    void deleteByItemId(UUID itemId);
}
