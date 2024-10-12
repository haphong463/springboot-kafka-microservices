package net.javaguides.product_service.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.product_service.dto.category.CategoryResponseDto;
import net.javaguides.product_service.dto.product_variant.ProductVariantResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private int version;
    private List<ProductVariantResponseDto> variants;
    private Set<CategoryResponseDto> categories;
}
