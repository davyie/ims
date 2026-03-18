package com.ims.api.controller;

import com.ims.api.dto.request.DecrementStockRequest;
import com.ims.api.dto.request.IncrementStockRequest;
import com.ims.api.dto.request.SetPriceRequest;
import com.ims.api.dto.request.ShiftItemRequest;
import com.ims.api.dto.response.MarketItemResponse;
import com.ims.api.dto.response.TransactionResponse;
import com.ims.application.command.DecrementStockCommand;
import com.ims.application.command.IncrementStockCommand;
import com.ims.application.command.SetPriceCommand;
import com.ims.application.command.ShiftItemCommand;
import com.ims.application.port.inbound.MarketItemQueryPort;
import com.ims.application.port.inbound.MarketStockCommandPort;
import com.ims.application.port.inbound.TransactionQueryPort;
import com.ims.application.query.GetTransactionHistoryQuery;
import com.ims.domain.model.MarketItem;
import com.ims.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/markets")
@Tag(name = "Market Stock", description = "Market stock management endpoints")
public class MarketStockController {

    private final MarketStockCommandPort marketStockCommandPort;
    private final TransactionQueryPort transactionQueryPort;
    private final MarketItemQueryPort marketItemQueryPort;

    public MarketStockController(MarketStockCommandPort marketStockCommandPort,
                                  TransactionQueryPort transactionQueryPort,
                                  MarketItemQueryPort marketItemQueryPort) {
        this.marketStockCommandPort = marketStockCommandPort;
        this.transactionQueryPort = transactionQueryPort;
        this.marketItemQueryPort = marketItemQueryPort;
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Shift item to market")
    public ResponseEntity<MarketItemResponse> shiftItem(@PathVariable UUID id,
            @Valid @RequestBody ShiftItemRequest request) {
        MarketItem mi = marketStockCommandPort.shiftItem(new ShiftItemCommand(
            id, request.itemId(), request.quantity(), request.marketPrice(), request.currency(),
            request.createdBy() != null ? request.createdBy() : "system"
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(mi));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Get all items in a market")
    public ResponseEntity<List<MarketItemResponse>> getMarketItems(@PathVariable UUID id) {
        return ResponseEntity.ok(marketItemQueryPort.getMarketItems(id)
                .stream().map(this::toResponse).toList());
    }

    @PatchMapping("/{id}/items/{itemId}/increment")
    @Operation(summary = "Increment market item stock")
    public ResponseEntity<MarketItemResponse> incrementStock(@PathVariable UUID id,
            @PathVariable UUID itemId, @Valid @RequestBody IncrementStockRequest request) {
        MarketItem mi = marketStockCommandPort.incrementStock(new IncrementStockCommand(
            id, itemId, request.quantity(), request.note(),
            request.createdBy() != null ? request.createdBy() : "system"
        ));
        return ResponseEntity.ok(toResponse(mi));
    }

    @PatchMapping("/{id}/items/{itemId}/decrement")
    @Operation(summary = "Decrement market item stock (record sale)")
    public ResponseEntity<MarketItemResponse> decrementStock(@PathVariable UUID id,
            @PathVariable UUID itemId, @Valid @RequestBody DecrementStockRequest request) {
        MarketItem mi = marketStockCommandPort.decrementStock(new DecrementStockCommand(
            id, itemId, request.quantity(), request.note(),
            request.createdBy() != null ? request.createdBy() : "system"
        ));
        return ResponseEntity.ok(toResponse(mi));
    }

    @PutMapping("/{id}/items/{itemId}/price")
    @Operation(summary = "Set market item price")
    public ResponseEntity<MarketItemResponse> setPrice(@PathVariable UUID id,
            @PathVariable UUID itemId, @Valid @RequestBody SetPriceRequest request) {
        MarketItem mi = marketStockCommandPort.setPrice(new SetPriceCommand(id, itemId, request.price(), request.currency()));
        return ResponseEntity.ok(toResponse(mi));
    }

    @GetMapping("/{id}/items/{itemId}/transactions")
    @Operation(summary = "Get transaction history for a market item")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable UUID id,
            @PathVariable UUID itemId) {
        List<TransactionResponse> txs = transactionQueryPort.getTransactionHistory(
                new GetTransactionHistoryQuery(id, itemId, 0, 100))
                .stream().map(this::toTxResponse).toList();
        return ResponseEntity.ok(txs);
    }

    private MarketItemResponse toResponse(MarketItem mi) {
        return new MarketItemResponse(mi.getId(), mi.getMarketId(), mi.getItemId(),
            mi.getAllocatedStock().quantity(), mi.getCurrentStock().quantity(),
            mi.getMarketPrice() != null ? mi.getMarketPrice().amount() : null,
            mi.getMarketPrice() != null ? mi.getMarketPrice().currency() : null);
    }

    private TransactionResponse toTxResponse(Transaction tx) {
        return new TransactionResponse(tx.getId(), tx.getMarketId(), tx.getItemId(),
            tx.getType().name(), tx.getQuantityDelta(), tx.getStockBefore(), tx.getStockAfter(),
            tx.getNote(), tx.getOccurredAt(), tx.getCreatedBy());
    }
}
