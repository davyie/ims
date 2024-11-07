package project.Infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;
import project.Domain.Product.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
