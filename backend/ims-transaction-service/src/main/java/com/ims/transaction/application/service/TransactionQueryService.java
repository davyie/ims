package com.ims.transaction.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.transaction.adapter.out.persistence.TransactionJpaRepository;
import com.ims.transaction.domain.model.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionJpaRepository repository;

    public TransactionQueryService(TransactionJpaRepository repository) {
        this.repository = repository;
    }

    public PageResponse<TransactionRecord> queryTransactions(
            UUID userId,
            String originService,
            String eventType,
            Instant from,
            Instant to,
            int page,
            int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recordedAt"));
        Page<TransactionRecord> result = repository.findByFilters(userId, originService, eventType, from, to, pageable);
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    public List<TransactionRecord> getCorrelationChain(UUID correlationId) {
        List<TransactionRecord> chain = repository.findByCorrelationIdOrderByRecordedAtAsc(correlationId);
        if (chain.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for correlationId: " + correlationId);
        }
        return chain;
    }
}
