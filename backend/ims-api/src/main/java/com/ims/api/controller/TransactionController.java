package com.ims.api.controller;

import com.ims.api.dto.response.TransactionResponse;
import com.ims.api.security.CurrentUserService;
import com.ims.application.port.inbound.TransactionQueryPort;
import com.ims.application.query.GetTransactionHistoryQuery;
import com.ims.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction history endpoints")
public class TransactionController {

    private final TransactionQueryPort transactionQueryUseCase;
    private final CurrentUserService currentUserService;

    public TransactionController(TransactionQueryPort transactionQueryUseCase, CurrentUserService currentUserService) {
        this.transactionQueryUseCase = transactionQueryUseCase;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "Get all transactions, optionally filtered by market and/or item")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) UUID marketId,
            @RequestParam(required = false) UUID itemId) {
        UUID userId = currentUserService.getCurrentUserId();
        List<TransactionResponse> result = transactionQueryUseCase
                .getTransactionHistory(new GetTransactionHistoryQuery(userId, marketId, itemId, 0, Integer.MAX_VALUE))
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(), tx.getMarketId(), tx.getItemId(),
                tx.getType().name(), tx.getQuantityDelta(),
                tx.getStockBefore(), tx.getStockAfter(),
                tx.getNote(), tx.getOccurredAt(), tx.getCreatedBy(),
                tx.getSalePrice(), tx.getSaleCurrency());
    }
}
