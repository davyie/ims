package com.ims.api.controller;

import com.ims.api.dto.request.AdjustStockRequest;
import com.ims.api.dto.request.RegisterItemRequest;
import com.ims.api.dto.request.UpdateItemRequest;
import com.ims.api.dto.response.ItemResponse;
import com.ims.api.dto.response.TransactionResponse;
import com.ims.api.security.CurrentUserService;
import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.application.port.inbound.ItemCommandPort;
import com.ims.application.port.inbound.ItemQueryPort;
import com.ims.application.port.inbound.TransactionQueryPort;
import com.ims.application.query.GetItemQuery;
import com.ims.application.query.GetTransactionHistoryQuery;
import com.ims.application.query.ListItemsQuery;
import com.ims.domain.model.Item;
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
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "Item management endpoints")
public class ItemController {

    private final ItemCommandPort registerItemUseCase;
    private final ItemCommandPort updateItemUseCase;
    private final ItemCommandPort adjustStockUseCase;
    private final ItemCommandPort deleteItemUseCase;
    private final ItemQueryPort itemQueryUseCase;
    private final TransactionQueryPort transactionQueryUseCase;
    private final CurrentUserService currentUserService;

    public ItemController(
            com.ims.application.usecase.item.RegisterItemUseCase registerItemUseCase,
            com.ims.application.usecase.item.UpdateItemUseCase updateItemUseCase,
            com.ims.application.usecase.item.AdjustStorageStockUseCase adjustStockUseCase,
            com.ims.application.usecase.item.DeleteItemUseCase deleteItemUseCase,
            ItemQueryPort itemQueryUseCase,
            TransactionQueryPort transactionQueryUseCase,
            CurrentUserService currentUserService) {
        this.registerItemUseCase = registerItemUseCase;
        this.updateItemUseCase = updateItemUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.deleteItemUseCase = deleteItemUseCase;
        this.itemQueryUseCase = itemQueryUseCase;
        this.transactionQueryUseCase = transactionQueryUseCase;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Register a new item")
    public ResponseEntity<ItemResponse> registerItem(@Valid @RequestBody RegisterItemRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Item item = registerItemUseCase.registerItem(new RegisterItemCommand(
            userId, request.sku(), request.name(), request.description(), request.category(),
            request.defaultPrice(), request.currency(),
            request.zone(), request.shelf(), request.row(), request.column(),
            request.initialStock()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(item));
    }

    @GetMapping
    @Operation(summary = "List all items")
    public ResponseEntity<List<ItemResponse>> listItems(@RequestParam(required = false) String category) {
        UUID userId = currentUserService.getCurrentUserId();
        List<ItemResponse> items = itemQueryUseCase.listItems(new ListItemsQuery(userId, category))
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<ItemResponse> getItem(@PathVariable UUID id) {
        UUID userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(toResponse(itemQueryUseCase.getItem(new GetItemQuery(userId, id))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable UUID id, @Valid @RequestBody UpdateItemRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Item item = updateItemUseCase.updateItem(new UpdateItemCommand(
            userId, id, request.name(), request.description(), request.category(),
            request.defaultPrice(), request.currency(),
            request.zone(), request.shelf(), request.row(), request.column()
        ));
        return ResponseEntity.ok(toResponse(item));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Adjust storage stock")
    public ResponseEntity<ItemResponse> adjustStock(@PathVariable UUID id, @Valid @RequestBody AdjustStockRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Item item = adjustStockUseCase.adjustStorageStock(new AdjustStorageStockCommand(
            userId, id, request.delta(), request.note(), request.createdBy()
        ));
        return ResponseEntity.ok(toResponse(item));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        deleteItemUseCase.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "Get transaction history for an item")
    public ResponseEntity<List<TransactionResponse>> getItemTransactions(@PathVariable UUID id) {
        UUID userId = currentUserService.getCurrentUserId();
        List<TransactionResponse> txs = transactionQueryUseCase.getTransactionHistory(
                new GetTransactionHistoryQuery(userId, null, id, 0, 100))
                .stream().map(this::toTransactionResponse).toList();
        return ResponseEntity.ok(txs);
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
            item.getId(), item.getSku(), item.getName(), item.getDescription(), item.getCategory(),
            item.getDefaultPrice() != null ? item.getDefaultPrice().amount() : null,
            item.getDefaultPrice() != null ? item.getDefaultPrice().currency() : null,
            item.getStoragePosition() != null ? item.getStoragePosition().zone() : null,
            item.getStoragePosition() != null ? item.getStoragePosition().shelf() : null,
            item.getStoragePosition() != null ? item.getStoragePosition().row() : 0,
            item.getStoragePosition() != null ? item.getStoragePosition().column() : 0,
            item.getTotalStorageStock().quantity(),
            item.getCreatedAt(), item.getUpdatedAt()
        );
    }

    private TransactionResponse toTransactionResponse(Transaction tx) {
        return new TransactionResponse(tx.getId(), tx.getMarketId(), tx.getItemId(),
            tx.getType().name(), tx.getQuantityDelta(), tx.getStockBefore(), tx.getStockAfter(),
            tx.getNote(), tx.getOccurredAt(), tx.getCreatedBy(),
            tx.getSalePrice(), tx.getSaleCurrency());
    }
}
