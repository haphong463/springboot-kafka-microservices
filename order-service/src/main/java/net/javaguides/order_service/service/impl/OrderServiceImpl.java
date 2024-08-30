package net.javaguides.order_service.service.impl;

import net.javaguides.base_domains.dto.order.OrderDTO;
import net.javaguides.base_domains.dto.order.OrderEvent;
import net.javaguides.base_domains.dto.order.OrderItemDTO;
import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderItem;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.kafka.OrderProducer;
import net.javaguides.order_service.repository.OrderRepository;
import net.javaguides.order_service.service.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderProducer orderProducer, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) {
        try {
            // Set a unique ID for the order
            orderDTO.setOrderId(UUID.randomUUID().toString());

            // Map the DTO to the Order entity
            Order order = modelMapper.map(orderDTO, Order.class);

            // Set the order reference in each order item
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.setOrder(order);
            }

            // Save the order and order items to the database
            Order createdOrder = orderRepository.save(order);

            // Create an order event
            OrderEvent orderEvent = createOrderEvent(createdOrder);

            // Send the order event to Kafka
            orderProducer.sendMessage(orderEvent);

            // Log success and return the created order as DTO
            LOGGER.info("Order created successfully with ID: {}", createdOrder.getOrderId());
            return modelMapper.map(createdOrder, OrderDTO.class);
        } catch (Exception e) {
            // Log the error and throw an OrderException
            LOGGER.error("Failed to create order: {}", e.getMessage(), e);
            throw new OrderException("Failed to create order: " + e.getMessage(), e);
        }
    }
    private OrderEvent createOrderEvent(Order createdOrder) {
        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDTO(createdOrderDto);
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is in pending state");
        return orderEvent;
    }
}
