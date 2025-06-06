package com.example.commands;

import com.example.DTO.ProductDTO;
import com.example.domain.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateProductCommand {

    private ProductRepository productRepository;

    @Autowired
    public CreateProductCommand(ProductRepository pr) {
        this.productRepository = pr;
    }

    public ProductDTO createProduct(ProductDTO productdto) {
        // Create product here
        Product p = new Product();
        p.setDescription(productdto.getDescription());
        p.setName(productdto.getName());
        productRepository.save(p);
        return productdto;
    }
}
