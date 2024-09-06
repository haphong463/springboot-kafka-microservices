package net.javaguides.stock_service.service;


import net.javaguides.base_domains.dto.order.OrderEvent;
import net.javaguides.base_domains.dto.product.ProductEvent;
import net.javaguides.stock_service.entity.Stock;

import java.util.List;
import java.util.Set;

public interface StockService {
    void createProductStock(ProductEvent productEvent);
    void updateProductStock(OrderEvent orderEvent);
    List<Stock> getProductsStock(Set<String> productIds);
}
