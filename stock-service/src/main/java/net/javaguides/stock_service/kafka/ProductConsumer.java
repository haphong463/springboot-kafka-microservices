package net.javaguides.stock_service.kafka;


import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.common_lib.dto.product.ProductEvent;
import net.javaguides.stock_service.service.ProductAPIClient;
import net.javaguides.stock_service.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);
    private final StockService stockService;
    private final ProductAPIClient productAPIClient;

    public ProductConsumer(StockService stockService, ProductAPIClient productAPIClient) {
        this.stockService = stockService;
        this.productAPIClient = productAPIClient;
    }

    @KafkaListener(topics = "${spring.kafka.product-topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ProductEvent productEvent){
        LOGGER.info(String.format("Product event consumed => %s", productEvent.toString()));

        // Tạo stock cho product
        ApiResponse<ProductDTO> productDTO = productAPIClient.getProductById(productEvent.getProductDTO().getId()).getBody();
        if(productDTO != null && productDTO.getData() != null) {
            stockService.createProductStock(productEvent);
            System.out.println("productDTO: " + productDTO);
        }else{
            LOGGER.warn(String.format("Product with ID => %s was not found!", productEvent.getProductDTO().getId()));
        }
    }
}
