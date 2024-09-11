package net.javaguides.product_service.service;



import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.product_service.dto.ProductStockResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductService {
    ProductStockResponse saveProduct(ProductDTO productDTO);
    ProductDTO getProductById(String id);
    List<ProductStockResponse> getProductList();
    ProductStockResponse updateProduct(String id, ProductDTO productDTO);
    ProductDTO deleteProduct(String id);
    List<ProductDTO> getProductsByIds(Set<String> productIds);
}
