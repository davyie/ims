package com.ims.warehouse.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.warehouse.domain.model.Item;
import com.ims.warehouse.domain.port.in.ItemUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemUseCase itemUseCase;

    public ItemController(ItemUseCase itemUseCase) {
        this.itemUseCase = itemUseCase;
    }

    record CreateItemRequest(@NotBlank String sku, @NotBlank String name,
                              String description, String category,
                              String unitOfMeasure, BigDecimal unitPrice) {}

    record UpdateItemRequest(String name, String description, String category,
                              String unitOfMeasure, BigDecimal unitPrice) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@Valid @RequestBody CreateItemRequest request, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return itemUseCase.createItem(request.sku(), request.name(), request.description(),
                request.category(), request.unitOfMeasure(), request.unitPrice(), userId);
    }

    @GetMapping
    public PageResponse<Item> listItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return itemUseCase.listItems(userId, page, size);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable UUID itemId) {
        return itemUseCase.getItemById(itemId);
    }

    @PutMapping("/{itemId}")
    public Item updateItem(@PathVariable UUID itemId,
                           @Valid @RequestBody UpdateItemRequest request,
                           Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return itemUseCase.updateItem(itemId, request.name(), request.description(),
                request.category(), request.unitOfMeasure(), request.unitPrice(), userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID itemId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        itemUseCase.deleteItem(itemId, userId);
    }
}
