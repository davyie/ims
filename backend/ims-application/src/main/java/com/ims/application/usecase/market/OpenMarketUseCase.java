package com.ims.application.usecase.market;

import com.ims.domain.event.MarketOpenedEvent;
import com.ims.domain.exception.MarketNotFoundException;
import com.ims.domain.model.Market;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.application.command.OpenMarketCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpenMarketUseCase {

    private final MarketRepositoryPort marketRepository;
    private final DomainEventPublisherPort eventPublisher;

    public OpenMarketUseCase(MarketRepositoryPort marketRepository, DomainEventPublisherPort eventPublisher) {
        this.marketRepository = marketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Market openMarket(OpenMarketCommand command) {
        Market market = marketRepository.findById(command.marketId())
                .orElseThrow(() -> new MarketNotFoundException(command.marketId()));

        market.open();
        Market saved = marketRepository.save(market);
        eventPublisher.publish(new MarketOpenedEvent(saved.getId(), saved.getName()));
        return saved;
    }
}
