package project.Domain.Product;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Getter(AccessLevel.PUBLIC)
    private String id;
    @Getter(AccessLevel.PUBLIC)
    private Long price;
}
