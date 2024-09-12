package net.javaguides.order_service.service.state;

import net.javaguides.order_service.entity.Order;

public interface OrderState {
    void handleStateChange(Order order);
}
