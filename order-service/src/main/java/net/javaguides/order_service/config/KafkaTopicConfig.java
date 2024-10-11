package net.javaguides.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.name}")
    private String orderTopic;

    @Value("${spring.kafka.create-order-topic.name}")
    private String createOrderTopic;

    //spring bean for kafka topic
    @Bean
    public NewTopic createOrderTopic(){
        return TopicBuilder.name(createOrderTopic).build();
    }
}
