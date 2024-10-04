package net.javaguides.order_service.service;


import net.javaguides.common_lib.dto.order.OrderDTO;
import net.javaguides.order_service.dto.OrderRequestDto;
import net.javaguides.order_service.dto.OrderResponseDto;
import net.javaguides.order_service.dto.OrderResponseDtoWithOutOrderItems;

import java.awt.print.Pageable;
import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(OrderRequestDto order, Long userId, String email);
    OrderResponseDto checkOrderStatusByOrderId(String orderId);
    OrderResponseDto updateOrderStatus(String orderId, int version);
    OrderDTO cancelOrder(String orderId, Long userId);
    List<OrderResponseDtoWithOutOrderItems> getAllOrders(Long userId, int page, int size);
}
