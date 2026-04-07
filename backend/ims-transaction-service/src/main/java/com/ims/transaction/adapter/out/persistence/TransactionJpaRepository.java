package com.ims.transaction.adapter.out.persistence;

import com.ims.transaction.domain.model.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionRecord, UUID> {

    Optional<TransactionRecord> findByEventId(UUID eventId);

    boolean existsByEventId(UUID eventId);

    Page<TransactionRecord> findByUserId(UUID userId, Pageable pageable);

    Page<TransactionRecord> findByOriginService(String originService, Pageable pageable);

    Page<TransactionRecord> findByEventType(String eventType, Pageable pageable);

    Page<TransactionRecord> findByEntityId(UUID entityId, Pageable pageable);

    List<TransactionRecord> findByCorrelationIdOrderByRecordedAtAsc(UUID correlationId);

    @Query("""
            SELECT t FROM TransactionRecord t
            WHERE (:userId IS NULL OR t.userId = :userId)
            AND (:originService IS NULL OR t.originService = :originService)
            AND (:eventType IS NULL OR t.eventType = :eventType)
            AND (:from IS NULL OR t.recordedAt >= :from)
            AND (:to IS NULL OR t.recordedAt <= :to)
            """)
    Page<TransactionRecord> findByFilters(
            @Param("userId") UUID userId,
            @Param("originService") String originService,
            @Param("eventType") String eventType,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );
}
