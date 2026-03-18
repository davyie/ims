package com.ims.application.usecase.market;

import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.application.port.inbound.MarketCommandPort;
import com.ims.domain.model.Market;
import com.ims.domain.port.MarketRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMarketUseCase implements MarketCommandPort {

    private final MarketRepositoryPort marketRepository;
    private final OpenMarketUseCase openMarketUseCase;
    private final CloseMarketUseCase closeMarketUseCase;

    public CreateMarketUseCase(MarketRepositoryPort marketRepository,
                                OpenMarketUseCase openMarketUseCase,
                                CloseMarketUseCase closeMarketUseCase) {
        this.marketRepository = marketRepository;
        this.openMarketUseCase = openMarketUseCase;
        this.closeMarketUseCase = closeMarketUseCase;
    }

    @Override
    @Transactional
    public Market createMarket(CreateMarketCommand command) {
        Market market = Market.create(command.name(), command.place(), command.openDate(), command.closeDate());
        return marketRepository.save(market);
    }

    @Override
    public Market openMarket(OpenMarketCommand command) {
        return openMarketUseCase.openMarket(command);
    }

    @Override
    public Market closeMarket(CloseMarketCommand command) {
        return closeMarketUseCase.closeMarket(command);
    }
}
