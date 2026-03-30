package com.ims.application.usecase.market;

import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.application.command.UpdateMarketCommand;
import com.ims.application.port.inbound.MarketCommandPort;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Market;
import com.ims.domain.port.MarketRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateMarketUseCase implements MarketCommandPort {

    private final MarketRepositoryPort marketRepository;

    public UpdateMarketUseCase(MarketRepositoryPort marketRepository) {
        this.marketRepository = marketRepository;
    }

    @Override
    @Transactional
    public Market updateMarket(UpdateMarketCommand command) {
        Market market = marketRepository.findById(command.marketId())
                .orElseThrow(() -> new MarketNotFoundException(command.marketId()));
        market.update(command.name(), command.place(), command.openDate(), command.closeDate());
        return marketRepository.save(market);
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
    public void deleteMarket(UUID id) {
        throw new UnsupportedOperationException("Handled by DeleteMarketUseCase");
    }
}
