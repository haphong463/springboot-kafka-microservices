package net.javaguides.email_service.kafka;

import net.javaguides.base_domains.dto.order.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @KafkaListener(topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent){
        LOGGER.info(String.format("OrderDTO event received in email service -> %s", orderEvent.toString()));
        //send an email to the customer
    }
}
