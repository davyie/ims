package com.ims.application.usecase.market;

import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.application.command.UpdateMarketCommand;
import com.ims.application.port.inbound.MarketCommandPort;
import com.ims.domain.exception.InvalidMarketStateException;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Market;
import com.ims.domain.model.MarketStatus;
import com.ims.domain.port.MarketRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteMarketUseCase implements MarketCommandPort {

    private final MarketRepositoryPort marketRepository;

    public DeleteMarketUseCase(MarketRepositoryPort marketRepository) {
        this.marketRepository = marketRepository;
    }

    @Override
    @Transactional
    public void deleteMarket(UUID id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new MarketNotFoundException(id));
        if (market.getStatus() == MarketStatus.OPEN) {
            throw new InvalidMarketStateException("Cannot delete an open market. Close it first.");
        }
        marketRepository.deleteById(id);
    }

    @Override
    public Market createMarket(CreateMarketCommand command) {
        throw new UnsupportedOperationException("Handled by CreateMarketUseCase");
    }

    @Override
    public Market openMarket(OpenMarketCommand command) {
        throw new UnsupportedOperationException("Handled by OpenMarketUseCase");
    }

    @Override
    public Market closeMarket(CloseMarketCommand command) {
        throw new UnsupportedOperationException("Handled by CloseMarketUseCase");
    }

    @Override
    public Market updateMarket(UpdateMarketCommand command) {
        throw new UnsupportedOperationException("Handled by UpdateMarketUseCase");
    }
}
