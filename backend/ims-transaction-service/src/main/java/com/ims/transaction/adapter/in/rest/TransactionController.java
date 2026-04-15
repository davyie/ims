package com.ims.transaction.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.transaction.application.service.TransactionQueryService;
import com.ims.transaction.domain.model.TransactionRecord;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionQueryService queryService;

    public TransactionController(TransactionQueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * GET /api/v1/transactions
     * Paginated, filterable query across the full transaction ledger.
     */
    @GetMapping
    public ResponseEntity<PageResponse<TransactionRecord>> queryTransactions(
            @RequestParam(name = "userId", required = false) UUID userId,
            @RequestParam(name = "service", required = false) String service,
            @RequestParam(name = "eventType", required = false) String eventType,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(queryService.queryTransactions(userId, service, eventType, from, to, page, size));
    }

    /**
     * GET /api/v1/transactions/{correlationId}/chain
     * Returns the full ordered event chain for a saga / correlation ID.
     */
    @GetMapping("/{correlationId}/chain")
    public ResponseEntity<List<TransactionRecord>> getCorrelationChain(@PathVariable("correlationId") UUID correlationId) {
        return ResponseEntity.ok(queryService.getCorrelationChain(correlationId));
    }
}
