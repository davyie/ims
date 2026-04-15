package com.ims.market.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.market.domain.model.Market;
import com.ims.market.domain.model.MarketStock;
import com.ims.market.domain.model.MarketType;
import com.ims.market.domain.port.in.MarketStockUseCase;
import com.ims.market.domain.port.in.MarketUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/markets")
public class MarketController {

    private final MarketUseCase marketUseCase;
    private final MarketStockUseCase marketStockUseCase;

    public MarketController(MarketUseCase marketUseCase, MarketStockUseCase marketStockUseCase) {
        this.marketUseCase = marketUseCase;
        this.marketStockUseCase = marketStockUseCase;
    }

    record CreateMarketRequest(@NotBlank String name, String location,
                                @NotNull MarketType marketType, String description) {}
    record UpdateMarketRequest(String name, String location, String description) {}
    record StockOperationRequest(@NotNull UUID itemId, @Positive int quantity) {}
    record SetupAdjustRequest(@NotNull UUID itemId, @NotNull Integer delta) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Market createMarket(@Valid @RequestBody CreateMarketRequest request, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return marketUseCase.createMarket(request.name(), request.location(), request.marketType(), request.description(), userId);
    }

    @GetMapping
    public PageResponse<Market> listMarkets(
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID requestingUserId = UUID.fromString(auth.getName());
        return marketUseCase.listMarkets(userId != null ? userId : requestingUserId, page, size);
    }

    @GetMapping("/{marketId}")
    public Market getMarket(@PathVariable UUID marketId) {
        return marketUseCase.getMarketById(marketId);
    }

    @PutMapping("/{marketId}")
    public Market updateMarket(@PathVariable UUID marketId,
                                @Valid @RequestBody UpdateMarketRequest request,
                                Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return marketUseCase.updateMarket(marketId, request.name(), request.location(), request.description(), userId);
    }

    @DeleteMapping("/{marketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMarket(@PathVariable UUID marketId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        marketUseCase.deleteMarket(marketId, userId);
    }

    @PostMapping("/{marketId}/open")
    public Market openMarket(@PathVariable UUID marketId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return marketUseCase.openMarket(marketId, userId);
    }

    @PostMapping("/{marketId}/close")
    public Market closeMarket(@PathVariable UUID marketId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return marketUseCase.closeMarket(marketId, userId);
    }

    @GetMapping("/{marketId}/stock")
    public PageResponse<MarketStock> listStock(
            @PathVariable UUID marketId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return marketStockUseCase.listStockByMarket(marketId, page, size);
    }

    @PostMapping("/{marketId}/stock/increment")
    public MarketStock incrementStock(@PathVariable UUID marketId,
                                       @Valid @RequestBody StockOperationRequest request) {
        return marketStockUseCase.incrementStock(marketId, request.itemId(), request.quantity());
    }

    @PostMapping("/{marketId}/stock/decrement")
    public MarketStock decrementStock(@PathVariable UUID marketId,
                                       @Valid @RequestBody StockOperationRequest request) {
        return marketStockUseCase.decrementStock(marketId, request.itemId(), request.quantity());
    }

    @PostMapping("/{marketId}/stock/setup-adjust")
    public MarketStock setupAdjustStock(@PathVariable UUID marketId,
                                         @Valid @RequestBody SetupAdjustRequest request) {
        return marketStockUseCase.setupAdjust(marketId, request.itemId(), request.delta());
    }
}
