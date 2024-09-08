package net.javaguides.product_service.dto;

import io.github.haphong463.dto.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStockResponse {
    private ProductDTO product;
    private StockDto stock;
}
