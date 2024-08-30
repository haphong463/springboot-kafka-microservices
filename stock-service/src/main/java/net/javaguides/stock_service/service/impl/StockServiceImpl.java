package net.javaguides.stock_service.service.impl;

import net.javaguides.base_domains.dto.product.ProductEvent;
import net.javaguides.stock_service.entity.Stock;
import net.javaguides.stock_service.repository.StockRepository;
import net.javaguides.stock_service.service.StockService;
import org.springframework.stereotype.Service;

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
    public void updateProductStock(String productId, int orderQty) {
        Stock stock = stockRepository.findByProductId(productId);
        if(stock != null){
            if(stock.getQty() > 0){
                stock.setQty(stock.getQty() - orderQty);
                stockRepository.save(stock);
            }
        }
    }
}
