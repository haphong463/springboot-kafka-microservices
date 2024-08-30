package net.javaguides.stock_service.kafka;

import net.javaguides.base_domains.dto.product.ProductEvent;
import net.javaguides.stock_service.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);
    private final StockService stockService;

    public ProductConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @KafkaListener(topics = "${spring.kafka.product-topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ProductEvent productEvent){
        LOGGER.info(String.format("Product event consumed => %s", productEvent.toString()));

        // Tạo stock cho product
        stockService.createProductStock(productEvent);
    }
}
