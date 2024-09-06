package net.javaguides.order_service.service;


import net.javaguides.base_domains.dto.order.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(OrderDTO order, Long userId);
    OrderDTO checkOrderStatusByOrderId(String orderId);
}
