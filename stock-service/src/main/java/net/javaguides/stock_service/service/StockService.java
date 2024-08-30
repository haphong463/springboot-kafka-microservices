package net.javaguides.stock_service.service;

import net.javaguides.base_domains.dto.product.ProductEvent;

public interface StockService {
    void createProductStock(ProductEvent productEvent);
    void updateProductStock(String productId, int orderQty);
}
