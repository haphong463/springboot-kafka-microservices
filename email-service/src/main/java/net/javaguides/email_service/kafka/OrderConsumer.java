package net.javaguides.email_service.kafka;


import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private EmailService emailService;


    @KafkaListener(topics = "${spring.kafka.order-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent){
        try {
            LOGGER.info(String.format("OrderDTO event received in payment service -> %s", orderEvent.toString()));

            emailService.sendOrderConfirmationEmail(orderEvent);

        }catch(Exception e){
            LOGGER.warn(String.format("Error message -> %s", e.getMessage()));
        }
    }
}