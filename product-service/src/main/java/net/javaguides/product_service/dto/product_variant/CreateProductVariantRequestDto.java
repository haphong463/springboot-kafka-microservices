package net.javaguides.product_service.dto.product_variant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductVariantRequestDto {
    private Map<String, String> attributes;
    private BigDecimal price;
    private String sku;
    private Integer initialStock;
    private Integer reorderLevel;
}
