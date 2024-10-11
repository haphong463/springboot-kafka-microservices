package net.javaguides.product_service.service;



import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.product_service.dto.product.CreateProductRequestDto;
import net.javaguides.product_service.dto.ProductStockResponse;
import net.javaguides.product_service.dto.product.ProductResponseDto;
import net.javaguides.product_service.dto.product.UpdateProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductResponseDto saveProduct(CreateProductRequestDto createProductRequestDto);
    ProductResponseDto getProductById(String id);
    Page<ProductResponseDto> getProductList(int page, int size);
    ProductResponseDto updateProduct(String id, UpdateProductRequestDto productUpdateDto, int version);
    void deleteProduct(String id);
    List<ProductResponseDto> getProductsByIds(Set<String> productIds);
    Page<ProductResponseDto> searchProducts(String name, String categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
