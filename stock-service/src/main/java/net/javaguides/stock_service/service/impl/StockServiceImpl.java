package net.javaguides.stock_service.service.impl;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.common_lib.dto.product.ProductEvent;
import net.javaguides.stock_service.entity.Stock;
import net.javaguides.stock_service.repository.StockRepository;
import net.javaguides.stock_service.service.StockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {
    private StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void createProductStock(ProductEvent productEvent) {
        Stock stock = new Stock();
        stock.setProductId(productEvent.getProductDTO().getId());
        stock.setQty(productEvent.getProductDTO().getStockQuantity());

        stockRepository.save(stock);
    }

    @Override
    public void updateStockBasedOrder(OrderEvent orderEvent) {
        // Lấy danh sách các productId và số lượng tương ứng từ đơn hàng
        Map<String, Integer> productQuantityMap = orderEvent.getOrderDTO().getOrderItems().stream()
                .collect(Collectors.toMap(OrderItemDTO::getProductId, OrderItemDTO::getQuantity, Integer::sum));

        // Lấy tất cả các bản ghi Stock liên quan trong một truy vấn
        List<Stock> stocks = stockRepository.findAllByProductIdIn(productQuantityMap.keySet());

        List<Stock> stocksToUpdate = new ArrayList<>();

        for (Stock stock : stocks) {
            Integer orderedQuantity = productQuantityMap.get(stock.getProductId());
            if (stock.getQty() >= orderedQuantity) {
                stock.setQty(stock.getQty() - orderedQuantity);
                stocksToUpdate.add(stock);
            } else {
                // Xử lý khi số lượng kho không đủ, ví dụ ném ngoại lệ
                try {
                    throw new Exception("Insufficient stock for productId: " + stock.getProductId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Lưu tất cả các bản ghi đã cập nhật trong một lần
        stockRepository.saveAll(stocksToUpdate);
    }

    @Override
    public List<Stock> getProductsStock(Set<String> productIds) {
        return stockRepository.findAllByProductIdIn(productIds);
    }

    @Override
    public Stock updateStockQuantity(String productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId);
        if(stock != null){
            stock.setQty(stock.getQty() + quantity);
            stockRepository.save(stock);
            return stock;
        }
        return null;
    }
}
