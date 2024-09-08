package net.javaguides.product_service.service;



import io.github.haphong463.dto.product.ProductDTO;
import net.javaguides.product_service.dto.ProductStockResponse;

import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO getProductById(String id);
    List<ProductStockResponse> getProductList();
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    ProductDTO deleteProduct(String id);
    List<ProductDTO> getProductsByIds(Set<String> productIds);
}
