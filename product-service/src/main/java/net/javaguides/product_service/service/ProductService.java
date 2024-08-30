package net.javaguides.product_service.service;

import net.javaguides.base_domains.dto.product.ProductDTO;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
}
