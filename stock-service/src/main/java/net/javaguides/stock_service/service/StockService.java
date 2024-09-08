package net.javaguides.stock_service.service;



import io.github.haphong463.dto.order.OrderEvent;
import io.github.haphong463.dto.product.ProductEvent;
import net.javaguides.stock_service.entity.Stock;

import java.util.List;
import java.util.Set;

public interface StockService {
    void createProductStock(ProductEvent productEvent);
    void updateStockBasedOrder(OrderEvent orderEvent);
    List<Stock> getProductsStock(Set<String> productIds);
    Stock updateStockQuantity(String productId, int quantity);
}
