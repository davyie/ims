package com.example.market.repository;

import com.example.market.domain.Market;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketRepository extends MongoRepository<Market, String> {
    Optional<Market> findByName(String name);
}
