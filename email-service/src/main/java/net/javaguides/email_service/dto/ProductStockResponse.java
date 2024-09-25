package net.javaguides.email_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStockResponse {
    private ProductResponseDto product;
    private StockResponseDto stock;
}
