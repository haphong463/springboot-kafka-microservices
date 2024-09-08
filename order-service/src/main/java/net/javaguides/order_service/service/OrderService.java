package net.javaguides.order_service.service;


import io.github.haphong463.dto.order.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(OrderDTO order, Long userId);
    OrderDTO checkOrderStatusByOrderId(String orderId);
}
