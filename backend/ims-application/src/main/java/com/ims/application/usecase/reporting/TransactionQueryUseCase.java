package com.ims.application.usecase.reporting;

import com.ims.application.port.inbound.TransactionQueryPort;
import com.ims.application.query.GetTransactionHistoryQuery;
import com.ims.domain.model.Transaction;
import com.ims.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransactionQueryUseCase implements TransactionQueryPort {

    private final TransactionRepositoryPort transactionRepository;

    public TransactionQueryUseCase(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getTransactionHistory(GetTransactionHistoryQuery query) {
        if (query.marketId() != null && query.itemId() != null) {
            return transactionRepository.findByMarketIdAndItemId(query.marketId(), query.itemId());
        } else if (query.marketId() != null) {
            return transactionRepository.findByMarketId(query.marketId());
        } else if (query.itemId() != null) {
            return transactionRepository.findByItemId(query.itemId());
        }
        return transactionRepository.findAll();
    }
}
