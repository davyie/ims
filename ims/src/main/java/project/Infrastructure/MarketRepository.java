package project.Infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.Domain.Market.Market;

import java.util.Optional;

@Repository
public interface MarketRepository extends MongoRepository<Market, String> {
    Optional<Market> findById(String uuid);
}
