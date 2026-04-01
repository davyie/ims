package com.ims.api.controller;

import com.ims.api.dto.request.CreateMarketRequest;
import com.ims.api.dto.request.UpdateMarketRequest;
import com.ims.api.dto.response.*;
import com.ims.api.security.CurrentUserService;
import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.application.command.UpdateMarketCommand;
import com.ims.application.dto.AllMarketsSummaryDto;
import com.ims.application.dto.MarketItemSummaryDto;
import com.ims.application.dto.MarketSummaryDto;
import com.ims.application.port.inbound.MarketCommandPort;
import com.ims.application.port.inbound.MarketQueryPort;
import com.ims.application.query.GetAllMarketsSummaryQuery;
import com.ims.application.query.GetMarketQuery;
import com.ims.application.query.GetMarketSummaryQuery;
import com.ims.application.query.ListMarketsQuery;
import com.ims.domain.model.Market;
import com.ims.domain.model.MarketStatus;
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
@Tag(name = "Markets", description = "Market management endpoints")
public class MarketController {

    private final MarketCommandPort marketCommandPort;
    private final MarketCommandPort updateMarketUseCase;
    private final MarketCommandPort deleteMarketUseCase;
    private final MarketQueryPort marketQueryPort;
    private final CurrentUserService currentUserService;

    public MarketController(
            com.ims.application.usecase.market.CreateMarketUseCase marketCommandPort,
            com.ims.application.usecase.market.UpdateMarketUseCase updateMarketUseCase,
            com.ims.application.usecase.market.DeleteMarketUseCase deleteMarketUseCase,
            MarketQueryPort marketQueryPort,
            CurrentUserService currentUserService) {
        this.marketCommandPort = marketCommandPort;
        this.updateMarketUseCase = updateMarketUseCase;
        this.deleteMarketUseCase = deleteMarketUseCase;
        this.marketQueryPort = marketQueryPort;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "Create a market")
    public ResponseEntity<MarketResponse> createMarket(@Valid @RequestBody CreateMarketRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Market market = marketCommandPort.createMarket(new CreateMarketCommand(
            userId, request.name(), request.place(), request.openDate(), request.closeDate()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(market));
    }

    @GetMapping
    @Operation(summary = "List markets")
    public ResponseEntity<List<MarketResponse>> listMarkets(@RequestParam(required = false) MarketStatus status) {
        UUID userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(marketQueryPort.listMarkets(new ListMarketsQuery(userId, status))
                .stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get market by ID")
    public ResponseEntity<MarketResponse> getMarket(@PathVariable UUID id) {
        UUID userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(toResponse(marketQueryPort.getMarket(new GetMarketQuery(userId, id))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a market (only in SCHEDULED state)")
    public ResponseEntity<MarketResponse> updateMarket(@PathVariable UUID id,
            @Valid @RequestBody UpdateMarketRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        Market market = updateMarketUseCase.updateMarket(new UpdateMarketCommand(
            userId, id, request.name(), request.place(), request.openDate(), request.closeDate()
        ));
        return ResponseEntity.ok(toResponse(market));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a market (not allowed when OPEN)")
    public ResponseEntity<Void> deleteMarket(@PathVariable UUID id) {
        deleteMarketUseCase.deleteMarket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/open")
    @Operation(summary = "Open a market")
    public ResponseEntity<MarketResponse> openMarket(@PathVariable UUID id) {
        UUID userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(toResponse(marketCommandPort.openMarket(new OpenMarketCommand(userId, id))));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close a market")
    public ResponseEntity<MarketResponse> closeMarket(@PathVariable UUID id,
            @RequestParam(defaultValue = "system") String createdBy) {
        UUID userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(toResponse(marketCommandPort.closeMarket(new CloseMarketCommand(userId, id, createdBy))));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get market summary")
    public ResponseEntity<MarketSummaryResponse> getMarketSummary(@PathVariable UUID id) {
        UUID userId = currentUserService.getCurrentUserId();
        MarketSummaryDto dto = marketQueryPort.getMarketSummary(new GetMarketSummaryQuery(userId, id));
        return ResponseEntity.ok(toSummaryResponse(dto));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get all markets summary")
    public ResponseEntity<AllMarketsSummaryResponse> getAllMarketsSummary(
            @RequestParam(required = false) MarketStatus status) {
        UUID userId = currentUserService.getCurrentUserId();
        AllMarketsSummaryDto dto = marketQueryPort.getAllMarketsSummary(new GetAllMarketsSummaryQuery(userId, status));
        List<MarketSummaryResponse> summaries = dto.markets().stream().map(this::toSummaryResponse).toList();
        return ResponseEntity.ok(new AllMarketsSummaryResponse(
            dto.totalMarkets(), dto.totalItemsSold(), dto.totalRevenue(), dto.currency(), summaries));
    }

    private MarketResponse toResponse(Market m) {
        return new MarketResponse(m.getId(), m.getName(), m.getPlace(),
            m.getOpenDate(), m.getCloseDate(), m.getStatus().name(), m.getCreatedAt());
    }

    private MarketSummaryResponse toSummaryResponse(MarketSummaryDto dto) {
        List<MarketItemSummaryResponse> items = dto.items().stream()
                .map(i -> new MarketItemSummaryResponse(
                    i.itemId(), i.itemName(), i.sku(), i.allocatedStock(),
                    i.currentStock(), i.sold(), i.revenue(), i.currency()))
                .toList();
        return new MarketSummaryResponse(dto.marketId(), dto.marketName(), dto.totalItemTypes(),
            dto.totalAllocatedStock(), dto.totalCurrentStock(), dto.totalSold(),
            dto.totalRevenue(), dto.currency(), items);
    }
}
