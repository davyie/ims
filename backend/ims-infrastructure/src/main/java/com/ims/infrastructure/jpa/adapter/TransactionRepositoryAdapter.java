package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.Transaction;
import com.ims.domain.model.TransactionType;
import com.ims.domain.port.TransactionRepositoryPort;
import com.ims.infrastructure.jpa.entity.TransactionJpaEntity;
import com.ims.infrastructure.jpa.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction tx) {
        return toDomain(jpaRepository.save(toEntity(tx)));
    }

    @Override
    public List<Transaction> findAllByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByMarketId(UUID marketId) {
        return jpaRepository.findByMarketId(marketId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByItemId(UUID itemId) {
        return jpaRepository.findByItemId(itemId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByMarketIdAndItemId(UUID marketId, UUID itemId) {
        return jpaRepository.findByMarketIdAndItemId(marketId, itemId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByItemId(UUID itemId) {
        jpaRepository.deleteByItemId(itemId);
    }

    private TransactionJpaEntity toEntity(Transaction tx) {
        TransactionJpaEntity e = new TransactionJpaEntity();
        e.setId(tx.getId());
        e.setUserId(tx.getUserId());
        e.setMarketId(tx.getMarketId());
        e.setItemId(tx.getItemId());
        e.setType(TransactionJpaEntity.TransactionTypeJpa.valueOf(tx.getType().name()));
        e.setQuantityDelta(tx.getQuantityDelta());
        e.setStockBefore(tx.getStockBefore());
        e.setStockAfter(tx.getStockAfter());
        e.setNote(tx.getNote());
        e.setOccurredAt(tx.getOccurredAt());
        e.setCreatedBy(tx.getCreatedBy());
        e.setSalePrice(tx.getSalePrice());
        e.setSaleCurrency(tx.getSaleCurrency());
        return e;
    }

    private Transaction toDomain(TransactionJpaEntity e) {
        return new Transaction(e.getId(), e.getUserId(), e.getMarketId(), e.getItemId(),
            TransactionType.valueOf(e.getType().name()),
            e.getQuantityDelta(), e.getStockBefore(), e.getStockAfter(),
            e.getNote(), e.getOccurredAt(), e.getCreatedBy(),
            e.getSalePrice(), e.getSaleCurrency());
    }
}
