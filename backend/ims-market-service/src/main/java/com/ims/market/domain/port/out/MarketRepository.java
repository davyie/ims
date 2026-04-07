package com.ims.market.domain.port.out;

import com.ims.market.domain.model.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MarketRepository {

    Market save(Market market);

    Optional<Market> findById(UUID marketId);

    Page<Market> findByUserId(UUID userId, Pageable pageable);

    Page<Market> findAll(Pageable pageable);
}
