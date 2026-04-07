package com.ims.market.adapter.out.persistence;

import com.ims.market.domain.model.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarketJpaRepository extends JpaRepository<Market, UUID> {

    Page<Market> findByUserId(UUID userId, Pageable pageable);
}
