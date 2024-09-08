package net.javaguides.product_service.dto;

import net.javaguides.common_lib.dto.product.ProductDTO;
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
