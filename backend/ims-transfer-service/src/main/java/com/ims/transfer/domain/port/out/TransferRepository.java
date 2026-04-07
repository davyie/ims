package com.ims.transfer.domain.port.out;

import com.ims.transfer.domain.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {

    Transfer save(Transfer transfer);

    Optional<Transfer> findById(UUID transferId);

    Optional<Transfer> findByCorrelationId(UUID correlationId);

    Page<Transfer> findAll(Pageable pageable);

    Page<Transfer> findByUserId(UUID userId, Pageable pageable);
}
