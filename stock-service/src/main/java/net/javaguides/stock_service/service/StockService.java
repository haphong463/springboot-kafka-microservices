package net.javaguides.stock_service.service;


import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.product.ProductEvent;
import net.javaguides.stock_service.entity.Stock;

import java.util.List;
import java.util.Set;

public interface StockService {
    void createProductStock(ProductEvent productEvent);

    void updateStockBasedOrder(OrderEvent orderEvent);

    List<Stock> getProductsStock(Set<String> productIds);

    Stock getProductStock(String productId);

    Stock updateStockQuantity(String productId, int quantity);

    void deleteProductStock(String productId);
}
