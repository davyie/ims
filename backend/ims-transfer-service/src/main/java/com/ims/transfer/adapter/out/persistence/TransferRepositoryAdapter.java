package com.ims.transfer.adapter.out.persistence;

import com.ims.transfer.domain.model.Transfer;
import com.ims.transfer.domain.port.out.TransferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TransferRepositoryAdapter implements TransferRepository {

    private final TransferJpaRepository jpaRepository;

    public TransferRepositoryAdapter(TransferJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transfer save(Transfer transfer) {
        return jpaRepository.save(transfer);
    }

    @Override
    public Optional<Transfer> findById(UUID transferId) {
        return jpaRepository.findById(transferId);
    }

    @Override
    public Optional<Transfer> findByCorrelationId(UUID correlationId) {
        return jpaRepository.findByCorrelationId(correlationId);
    }

    @Override
    public Page<Transfer> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Transfer> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }
}
