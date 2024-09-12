package net.javaguides.order_service.service;


import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.order_service.dto.OrderRequestDto;

public interface OrderService {
    OrderDTO placeOrder(OrderRequestDto order, Long userId);
    OrderDTO checkOrderStatusByOrderId(String orderId);
    OrderDTO updateOrderStatus(String orderId);
}
