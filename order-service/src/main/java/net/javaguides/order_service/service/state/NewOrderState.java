package net.javaguides.order_service.service.state;

import net.javaguides.order_service.entity.Order;
import net.javaguides.order_service.entity.OrderStatus;

public class NewOrderState implements OrderState{

    @Override
    public void handleStateChange(Order order) {
        order.setStatus(OrderStatus.PROCESSING.getLabel());
    }
}

