package net.javaguides.product_service.service;



import net.javaguides.base_domains.dto.product.ProductDTO;

import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO getProductById(String id);
    List<ProductDTO> getProductList();
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    ProductDTO deleteProduct(String id);
    List<ProductDTO> getProductsByIds(Set<String> productIds);
}
