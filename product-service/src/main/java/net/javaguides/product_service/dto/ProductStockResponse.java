package net.javaguides.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.product_service.dto.product.ProductResponseDto;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStockResponse {
    private ProductResponseDto product;
}
