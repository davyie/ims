package com.ims.transfer.adapter.out.persistence;

import com.ims.transfer.domain.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferJpaRepository extends JpaRepository<Transfer, UUID> {

    Optional<Transfer> findByCorrelationId(UUID correlationId);

    Page<Transfer> findByUserId(UUID userId, Pageable pageable);
}
