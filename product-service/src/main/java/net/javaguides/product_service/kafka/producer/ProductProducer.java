package net.javaguides.product_service.kafka.producer;

import net.javaguides.common_lib.dto.product.ProductEvent;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProductProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProducer.class);
    private NewTopic topic;
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductProducer(NewTopic topic, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(ProductEvent productEvent){
        LOGGER.info(String.format("ProductDTO event => %s", productEvent.toString()));


        //create message
        Message<ProductEvent> message = MessageBuilder.withPayload(productEvent)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }

    public void sendDeleteProductMessage(ProductEvent productEvent){
        Message<ProductEvent> message = MessageBuilder.withPayload(productEvent)
                        .setHeader(KafkaHeaders.TOPIC, topic.name())
                                .build();
        kafkaTemplate.send(message);
    }
}
