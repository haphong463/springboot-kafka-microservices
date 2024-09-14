package net.javaguides.order_service.service.impl;


import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.order_service.dto.OrderRequestDto;
import net.javaguides.order_service.dto.OrderResponseDto;
import net.javaguides.order_service.dto.PaymentDto;
import net.javaguides.order_service.dto.StockDto;
import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderItem;
import net.javaguides.order_service.entity.OrderStatus;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.kafka.OrderProducer;
import net.javaguides.order_service.repository.OrderRepository;
import net.javaguides.order_service.service.OrderService;
import net.javaguides.order_service.service.PaymentAPIClient;
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
    private final PaymentAPIClient paymentAPIClient;


    @Override
    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto, Long userId) {
        try {
            OrderDTO newOrder = createOrderDTO(orderRequestDto, userId);

            validateStockAndPrice(orderRequestDto, newOrder);

            Order createdOrder = saveOrder(newOrder);
            sendOrderEvent(createdOrder, orderRequestDto.getPaymentMethod());

            return createOrderResponseDto(createdOrder);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to create order: {}", e.getMessage(), e);
            throw new OrderException("Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponseDto checkOrderStatusByOrderId(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(orderId).getBody().getData();

            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderDTO(orderDTO);
            orderResponseDto.setPaymentDto(paymentDto);

            return orderResponseDto;
        }
        return null;
    }

    @Override
    public OrderResponseDto updateOrderStatus(String orderId, int version) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null){
            if(order.getVersion() != version){
                throw new OptimisticLockException("Version conflict!");
            }

            OrderContext context = new OrderContext(order);
            context.handleStateChange(order);

            OrderDTO orderDTO = modelMapper.map(orderRepository.save(order), OrderDTO.class);

            PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(orderId).getBody().getData();

            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderDTO(orderDTO);
            orderResponseDto.setPaymentDto(paymentDto);
            return orderResponseDto;
        }
        return null;
    }

    private OrderEvent createOrderEvent(Order createdOrder, String paymentMethod) {
        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDTO(createdOrderDto);
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is in pending state");
        orderEvent.setPaymentMethod(paymentMethod);
        return orderEvent;
    }

    private OrderDTO createOrderDTO(OrderRequestDto orderRequestDto, Long userId) {
        OrderDTO newOrder = modelMapper.map(orderRequestDto, OrderDTO.class);
        newOrder.setOrderId(UUID.randomUUID().toString());
        newOrder.setUserId(userId);
        return newOrder;
    }

    private void validateStockAndPrice(OrderRequestDto orderRequestDto, OrderDTO newOrder) {
        Set<String> productIds = orderRequestDto.getOrderItems()
                .stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toSet());

        List<StockDto> stockDtos = stockAPIClient.getProductsStock(productIds).getBody();
        ApiResponse<List<ProductDTO>> productDTOs = productAPIClient.getProductsByIds(productIds).getBody();

        for (OrderItemDTO orderItemDTO : orderRequestDto.getOrderItems()) {
            ProductDTO productDTO = getProductDTO(productDTOs, orderItemDTO.getProductId());
            StockDto stockDto = getStockDTO(stockDtos, orderItemDTO.getProductId());

            checkStockAvailability(orderItemDTO, stockDto);

            orderItemDTO.setPrice(productDTO.getPrice());
        }
    }

    private ProductDTO getProductDTO(ApiResponse<List<ProductDTO>> productDTOs, String productId) {
        assert productDTOs != null;
        return productDTOs.getData().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new OrderException("Product not found for ID: " + productId, HttpStatus.BAD_REQUEST));
    }

    private StockDto getStockDTO(List<StockDto> stockDtos, String productId) {
        return stockDtos.stream()
                .filter(stock -> stock.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new OrderException("Stock information not found for product: " + productId, HttpStatus.BAD_REQUEST));
    }

    private void checkStockAvailability(OrderItemDTO orderItemDTO, StockDto stockDto) {
        if (stockDto.getQty() < orderItemDTO.getQuantity()) {
            String errorMessage = String.format("Product %s is out of stock or insufficient quantity.", orderItemDTO.getProductId());
            LOGGER.error(errorMessage);
            throw new OrderException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    private Order saveOrder(OrderDTO newOrder) {
        Order order = modelMapper.map(newOrder, Order.class);
        order.setStatus(OrderStatus.PENDING.getLabel());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
        }
        return orderRepository.save(order);
    }

    private void sendOrderEvent(Order createdOrder, String paymentMethod) {
        OrderEvent orderEvent = createOrderEvent(createdOrder, paymentMethod);
        orderProducer.sendMessage(orderEvent);
    }

    private OrderResponseDto createOrderResponseDto(Order createdOrder) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(createdOrder.getOrderId()).getBody().getData();
        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
        orderResponseDto.setPaymentDto(paymentDto);
        orderResponseDto.setOrderDTO(createdOrderDto);
        LOGGER.info("Order created successfully with ID: {}", createdOrder.getOrderId());
        return orderResponseDto;
    }

}
