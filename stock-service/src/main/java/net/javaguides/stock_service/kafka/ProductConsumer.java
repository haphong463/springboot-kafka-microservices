package net.javaguides.stock_service.kafka;


import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.common_lib.dto.product.ProductEvent;
import net.javaguides.common_lib.dto.product.ProductMethod;
import net.javaguides.stock_service.service.ProductAPIClient;
import net.javaguides.stock_service.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);
    private final StockService stockService;
    private final ProductAPIClient productAPIClient;

    public ProductConsumer(StockService stockService, ProductAPIClient productAPIClient) {
        this.stockService = stockService;
        this.productAPIClient = productAPIClient;
    }

    @KafkaListener(topics = "${spring.kafka.product-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ProductEvent productEvent) {
        LOGGER.info(String.format("Product event consumed => %s", productEvent.toString()));

        switch (productEvent.getMethod()) {
            case CREATE:
                ApiResponse<ProductDTO> response = productAPIClient.getProductById(productEvent.getProductDTO().getId()).getBody();

                if (response == null || response.getData() == null) {
                    LOGGER.warn(String.format("Product with ID => %s was not found!", productEvent.getProductDTO().getId()));
                    return;
                }

                ProductDTO productDTO = response.getData();
                LOGGER.info(String.format("Fetched ProductDTO => %s", productDTO.toString()));

                stockService.createProductStock(productEvent);
                LOGGER.info(String.format("Stock created for Product ID => %s", productEvent.getProductDTO().getId()));
                break;

            case UPDATE:
                stockService.updateStockQuantity(productEvent.getProductDTO().getId(), productEvent.getProductDTO().getStockQuantity());
                LOGGER.info(String.format("Stock updated for Product ID => %s", productEvent.getProductDTO().getId()));
                break;

            case DELETE:
                stockService.deleteProductStock(productEvent.getProductDTO().getId());
                LOGGER.info(String.format("Stock deleted for Product ID => %s", productEvent.getProductDTO().getId()));
                break;

            default:
                LOGGER.warn(String.format("Unknown method for Product ID => %s", productEvent.getProductDTO().getId()));
                break;
        }
    }
}
