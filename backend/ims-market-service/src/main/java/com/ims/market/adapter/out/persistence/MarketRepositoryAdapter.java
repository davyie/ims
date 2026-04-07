package com.ims.market.adapter.out.persistence;

import com.ims.market.domain.model.Market;
import com.ims.market.domain.port.out.MarketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MarketRepositoryAdapter implements MarketRepository {

    private final MarketJpaRepository jpaRepository;

    public MarketRepositoryAdapter(MarketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Market save(Market market) {
        return jpaRepository.save(market);
    }

    @Override
    public Optional<Market> findById(UUID marketId) {
        return jpaRepository.findById(marketId);
    }

    @Override
    public Page<Market> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Market> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }
}
