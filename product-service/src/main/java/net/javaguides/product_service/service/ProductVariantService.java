package net.javaguides.product_service.service;

import net.javaguides.product_service.dto.product.ProductResponseDto;
import net.javaguides.product_service.dto.product_variant.ProductVariantResponseDto;
import net.javaguides.product_service.dto.product_variant.UpdateProductVariantRequestDto;
import net.javaguides.product_service.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductVariantService {
    ProductVariantResponseDto createProductVariant(String productId, Map<String, String> attributes, BigDecimal price, String sku, Integer initialStock, Integer reorderLevel);
    List<ProductVariantResponseDto> getVariantsByProductId(String productId);
    ProductVariantResponseDto updateProductVariant(Long variantId, UpdateProductVariantRequestDto updateDTO);
    void deleteProductVariant(Long variantId);
    List<ProductVariant> getProductVariantByIds(Set<Long> variantIds);
    ProductVariant getVariantById(Long variantId);
    void saveProductVariant(ProductVariant productVariant);
}
