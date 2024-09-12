package net.javaguides.order_service.service.impl;


import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.order_service.dto.OrderRequestDto;
import net.javaguides.order_service.dto.StockDto;
import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderItem;
import net.javaguides.order_service.entity.OrderStatus;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.kafka.OrderProducer;
import net.javaguides.order_service.repository.OrderRepository;
import net.javaguides.order_service.service.OrderService;
import net.javaguides.order_service.service.ProductAPIClient;
import net.javaguides.order_service.service.StockAPIClient;
import net.javaguides.order_service.service.state.OrderContext;
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
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final StockAPIClient stockAPIClient;
    private final ProductAPIClient productAPIClient;

    @Override
    public OrderDTO placeOrder(OrderRequestDto orderDTO, Long userId) {
        try {
            OrderDTO newOrder = modelMapper.map(orderDTO, OrderDTO.class);

            Set<String> productIds = orderDTO.getOrderItems()
                    .stream()
                    .map(OrderItemDTO::getProductId)
                    .collect(Collectors.toSet());

            List<StockDto> stockDtos = stockAPIClient.getProductsStock(productIds).getBody();

            ApiResponse<List<ProductDTO>> productDTOs = productAPIClient.getProductsByIds(productIds).getBody();

            for (OrderItemDTO orderItemDTO : orderDTO.getOrderItems()) {

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

                orderItemDTO.setPrice(productDTO.getPrice());
            }

            newOrder.setOrderId(UUID.randomUUID().toString());
            newOrder.setUserId(userId);

            Order order = modelMapper.map(newOrder, Order.class);
            order.setStatus(OrderStatus.PENDING.getLabel());

            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.setOrder(order);
            }

            Order createdOrder = orderRepository.save(order);

            OrderEvent orderEvent = createOrderEvent(createdOrder);

            orderProducer.sendMessage(orderEvent);

            LOGGER.info("Order created successfully with ID: {}", createdOrder.getOrderId());
            return modelMapper.map(createdOrder, OrderDTO.class);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
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

    @Override
    public OrderDTO updateOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null){
            OrderContext context = new OrderContext(order);
            context.handleStateChange(order);
            return modelMapper.map(orderRepository.save(order), OrderDTO.class);
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
