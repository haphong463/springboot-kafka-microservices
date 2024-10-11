package net.javaguides.product_service.dto.product_variant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.product_service.dto.attribute_value.AttributeValueResponseDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductVariantRequestDto {
    private Set<AttributeValueResponseDto> attributeValues;
    private BigDecimal price;
    private String sku;
    private Integer stockQuantity;
    private Integer reorderLevel;
}
