package com.ims.domain.port;

import com.ims.domain.model.Market;
import com.ims.domain.model.MarketStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketRepositoryPort {
    Market save(Market market);
    Optional<Market> findById(UUID id);
    List<Market> findAllByUserId(UUID userId, MarketStatus status);
    void deleteById(UUID id);
}
