package com.example.queries;

import com.example.DTO.ProductDTO;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllProductQuery {

    private ProductRepository productRepository;

    @Autowired
    public GetAllProductQuery(ProductRepository pr) {
        this.productRepository = pr;
    }

    public List<ProductDTO> run() {
        return productRepository.findAll().stream().map(
                p -> new ProductDTO(p.getName(), p.getDescription())).toList();
    }
}
