package com.ims.transaction.adapter.out.persistence;

import com.ims.transaction.domain.model.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionRecord, UUID>,
        JpaSpecificationExecutor<TransactionRecord> {

    Optional<TransactionRecord> findByEventId(UUID eventId);

    boolean existsByEventId(UUID eventId);

    Page<TransactionRecord> findByUserId(UUID userId, Pageable pageable);

    Page<TransactionRecord> findByOriginService(String originService, Pageable pageable);

    Page<TransactionRecord> findByEventType(String eventType, Pageable pageable);

    Page<TransactionRecord> findByEntityId(UUID entityId, Pageable pageable);

    List<TransactionRecord> findByCorrelationIdOrderByRecordedAtAsc(UUID correlationId);
}
