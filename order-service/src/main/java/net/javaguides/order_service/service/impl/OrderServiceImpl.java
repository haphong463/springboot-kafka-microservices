package net.javaguides.order_service.service.impl;


import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.order_service.dto.*;
import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderItem;
import net.javaguides.order_service.entity.OrderStatus;
import net.javaguides.order_service.exception.OrderException;
import net.javaguides.order_service.kafka.OrderProducer;
import net.javaguides.order_service.paypal.PayPalService;
import net.javaguides.order_service.redis.OrderRedis;
import net.javaguides.order_service.repository.OrderRepository;
import net.javaguides.order_service.service.OrderService;
import net.javaguides.order_service.service.PaymentAPIClient;
import net.javaguides.order_service.service.ProductAPIClient;
import net.javaguides.order_service.service.StockAPIClient;
import net.javaguides.order_service.service.state.OrderContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final PayPalService payPalService;
    private final OrderRedis orderRedis;

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequestDto orderRequestDto, Long userId, String email) {
        try {
            if(orderRequestDto.getPaymentMethod().equals("Paypal") && orderRequestDto.getOrderId() == null){
                throw new OrderException("Please provide Paypal's ID", HttpStatus.BAD_REQUEST);
            }

            OrderDTO newOrder = createOrderDTO(orderRequestDto, userId);
            validateStockAndPrice(orderRequestDto, newOrder);

            Order createdOrder = saveOrder(newOrder, orderRequestDto);
            orderRedis.save(modelMapper.map(createdOrder, OrderDTO.class));


            sendOrderEvent(createdOrder, orderRequestDto.getPaymentMethod(), email);

            return modelMapper.map(createdOrder, OrderDTO.class);
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to create order: {}", e.getMessage(), e);
            throw new OrderException("Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponseDto checkOrderStatusByOrderId(String orderId) {
        // Kiểm tra từ Redis cache
        OrderDTO cachedOrder = orderRedis.findByOrderId(orderId);
        OrderDTO order;

        if (cachedOrder != null) {
            order = cachedOrder;
            LOGGER.info("Order retrieved from Redis cache.");
        } else {
            // Nếu không tìm thấy trong cache, truy vấn cơ sở dữ liệu và lưu vào cache
            order = modelMapper.map(orderRepository.findById(orderId).orElse(null), OrderDTO.class);
            if (order != null) {
                orderRedis.save(order);
                LOGGER.info("Order retrieved from DB and saved to Redis cache.");
            }
        }

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
        if (order != null) {
            if (order.getVersion() != version) {
                throw new OptimisticLockException("Version conflict!");
            }

            OrderContext context = new OrderContext(order);
            context.handleStateChange(order);

            Order savedOrder = orderRepository.save(order);
            OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

            // Cập nhật cache Redis
            orderRedis.save(orderDTO);

            PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(orderId).getBody().getData();

            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setOrderDTO(orderDTO);
            orderResponseDto.setPaymentDto(paymentDto);
            return orderResponseDto;
        }
        return null;
    }


    //* Pre Authorized: ADMINISTRATOR, EMPLOYEE
    @Override
    public OrderDTO cancelOrder(String orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.CANCELED.getLabel());
            ApiResponse<PaymentDto> paymentDto = paymentAPIClient.getPaymentByOrderId(orderId).getBody();
            if(paymentDto != null && paymentDto.getData() != null && paymentDto.getData().getStatus().equals("Paypal")){
                try {
                    String captureId = payPalService.getCaptureIdFromOrder(orderId);
                    if(captureId != null){
                        sendRefundOrderEvent(order);
                        payPalService.refundPayment(captureId);
                    }
                }catch(Exception e){
                    throw new OrderException("No captures found for this order. Refund cannot be processed.", HttpStatus.BAD_REQUEST);
                }
            }
            OrderDTO savedOrderDTO = modelMapper.map(orderRepository.save(order), OrderDTO.class);
            orderRedis.save(savedOrderDTO);
            return savedOrderDTO;
        }
        return null;
    }

    @Override
    public List<OrderResponseDtoWithOutOrderItems> getAllOrders(Long userId, int page, int size) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, PageRequest.of(page, size));
        return orderPage.stream().map(order -> {
            // Lấy đơn hàng từ cache nếu có
            OrderDTO cachedOrder = orderRedis.findByOrderId(order.getOrderId());
            OrderWithOutOrderItems orderDTO = modelMapper.map(order, OrderWithOutOrderItems.class);
            if (cachedOrder == null) {
                // Nếu không có trong cache, lưu vào cache
                orderRedis.save(modelMapper.map(order, OrderDTO.class));
            }
            PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(order.getOrderId()).getBody().getData();
            return new OrderResponseDtoWithOutOrderItems(orderDTO, paymentDto);
        }).collect(Collectors.toList());
    }


    private OrderEvent createOrderEvent(Order createdOrder, String paymentMethod, String email) {
        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderDTO(createdOrderDto);
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is in pending state");
        orderEvent.setPaymentMethod(paymentMethod);
        orderEvent.setEmail(email);
        return orderEvent;
    }

    private OrderDTO createOrderDTO(OrderRequestDto orderRequestDto, Long userId) {
        OrderDTO newOrder = modelMapper.map(orderRequestDto, OrderDTO.class);
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

    private Order saveOrder(OrderDTO newOrder, OrderRequestDto orderRequestDto) {
        Order order = modelMapper.map(newOrder, Order.class);
        if ("Paypal".equalsIgnoreCase(orderRequestDto.getPaymentMethod())) {
            BigDecimal amount = BigDecimal.valueOf(0);

            for (OrderItemDTO orderItemDTO : orderRequestDto.getOrderItems()) {
                BigDecimal itemTotal = orderItemDTO.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.getQuantity()));
                amount = amount.add(itemTotal);
            }
            order.setOrderId(orderRequestDto.getOrderId());
        } else {
            order.setOrderId(UUID.randomUUID().toString());
        }
        order.setStatus(OrderStatus.PENDING.getLabel());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
        }
        return orderRepository.save(order);
    }

    private void sendOrderEvent(Order createdOrder, String paymentMethod, String email) {
        OrderEvent orderEvent = createOrderEvent(createdOrder, paymentMethod, email);
        orderProducer.sendMessage(orderEvent);
    }

    // ! This method is deprecated.
//    private OrderResponseDto createOrderResponseDto(Order createdOrder, OrderRequestDto orderRequestDto) {
//        OrderResponseDto orderResponseDto = new OrderResponseDto();
//        PaymentDto paymentDto = paymentAPIClient.getPaymentByOrderId(createdOrder.getOrderId()).getBody().getData();
//        OrderDTO createdOrderDto = modelMapper.map(createdOrder, OrderDTO.class);
//        orderResponseDto.setPaymentDto(paymentDto);
//        orderResponseDto.setOrderDTO(createdOrderDto);
//        LOGGER.info("Order created successfully with ID: {}", createdOrder.getOrderId());
//        return orderResponseDto;
//    }

    private void sendRefundOrderEvent(Order order){
        OrderEvent orderEvent = new OrderEvent();
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderEvent.setOrderDTO(orderDTO);
        orderEvent.setMessage("REFUND");
    }

}
