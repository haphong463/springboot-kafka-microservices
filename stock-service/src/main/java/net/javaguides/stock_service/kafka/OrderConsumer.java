package net.javaguides.stock_service.kafka;


import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.stock_service.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final StockService stockService;

    public OrderConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @KafkaListener(topics = "${spring.kafka.order-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent){
        try {
            LOGGER.info(String.format("OrderDTO event received in stock service -> %s", orderEvent.toString()));
            if(orderEvent.getMessage().equals("REFUND")){
                stockService.revertStockBasedOnCanceledOrder(orderEvent);
                return;
            }
            stockService.updateStockBasedOrder(orderEvent);
        }catch(Exception e){
           LOGGER.warn(String.format("Error message -> %s", e.getMessage()));
        }
    }
}
