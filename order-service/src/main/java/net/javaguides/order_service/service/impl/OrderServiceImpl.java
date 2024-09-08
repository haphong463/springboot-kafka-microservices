package net.javaguides.order_service.service.impl;


import io.github.haphong463.dto.ApiResponse;
import io.github.haphong463.dto.order.OrderDTO;
import io.github.haphong463.dto.order.OrderEvent;
import io.github.haphong463.dto.order.OrderItemDTO;
import io.github.haphong463.dto.product.ProductDTO;
import net.javaguides.order_service.dto.StockDto;
import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderItem;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.kafka.OrderProducer;
import net.javaguides.order_service.repository.OrderRepository;
import net.javaguides.order_service.service.OrderService;
import net.javaguides.order_service.service.ProductAPIClient;
import net.javaguides.order_service.service.StockAPIClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final StockAPIClient stockAPIClient;
    private final ProductAPIClient productAPIClient;

    public OrderServiceImpl(OrderProducer orderProducer, OrderRepository orderRepository, ModelMapper modelMapper, StockAPIClient stockAPIClient, ProductAPIClient productAPIClient) {
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.stockAPIClient = stockAPIClient;
        this.productAPIClient = productAPIClient;
    }

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO, Long userId) {
        try {
            // get the list of productIds from orderItems
            Set<String> productIds = orderDTO.getOrderItems()
                    .stream()
                    .map(OrderItemDTO::getProductId)
                    .collect(Collectors.toSet());

            // call the API to get stock and product information in a single request
            List<StockDto> stockDtos = stockAPIClient.getProductsStock(productIds).getBody();
            ApiResponse<List<ProductDTO>> productDTOs = productAPIClient.getProductsByIds(productIds).getBody();

            // check stock and set product price for each orderItem
            for (OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {
                // Get product information from productDTOs
                assert productDTOs != null;
                ProductDTO productDTO = productDTOs.getData().stream()
                        .filter(product -> product.getId().equals(orderItemDTO.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new OrderException("Product not found for ID: " + orderItemDTO.getProductId(), HttpStatus.BAD_REQUEST));

                // check stock availability
                StockDto stockDto = stockDtos.stream()
                        .filter(stock -> stock.getProductId().equals(orderItemDTO.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new OrderException("Stock information not found for product: " + orderItemDTO.getProductId(), HttpStatus.BAD_REQUEST));

                if (stockDto.getQty() < orderItemDTO.getQuantity()) {
                    String errorMessage = String.format("Product %s is out of stock or insufficient quantity.", orderItemDTO.getProductId());
                    LOGGER.error(errorMessage);
                    throw new OrderException(errorMessage, HttpStatus.BAD_REQUEST);
                }

                // set product price from ProductDTO into orderItemDTO
                orderItemDTO.setPrice(productDTO.getPrice());
            }

            // Set a unique ID for the order
            orderDTO.setOrderId(UUID.randomUUID().toString());
            orderDTO.setUserId(userId);

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
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            // Log the error and throw an OrderException
            LOGGER.error("Failed to create order: {}", e.getMessage(), e);
            throw new OrderException("Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    public OrderDTO checkOrderStatusByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            return modelMapper.map(order, OrderDTO.class);
        }
        return null;
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
