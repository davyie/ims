package com.ims.transfer.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.transfer.application.service.TransferService;
import com.ims.transfer.domain.model.LocationType;
import com.ims.transfer.domain.model.Transfer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    record CreateTransferRequest(
            @NotNull UUID itemId,
            @Positive int quantity,
            @NotNull LocationType sourceType,
            @NotNull UUID sourceId,
            @NotNull LocationType destinationType,
            @NotNull UUID destinationId
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer createTransfer(@Valid @RequestBody CreateTransferRequest request,
                                   Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return transferService.initiateTransfer(
                request.itemId(), request.quantity(),
                request.sourceType(), request.sourceId(),
                request.destinationType(), request.destinationId(),
                userId
        );
    }

    @GetMapping
    public PageResponse<Transfer> listTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return transferService.listTransfers(page, size);
    }

    @GetMapping("/{transferId}")
    public Transfer getTransfer(@PathVariable UUID transferId) {
        return transferService.getTransferById(transferId);
    }
}
