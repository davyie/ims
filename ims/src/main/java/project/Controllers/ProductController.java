package project.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.DTO.ProductDTO;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    public void createProduct(ProductDTO productDTO) {

    }
}
