package net.javaguides.order_service.service.state;

import net.javaguides.order_service.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderContext.class);
    private OrderState orderState;

    public OrderContext(Order order){
        switch(order.getStatus()){
            case "Pending":
                this.orderState = new NewOrderState();
                break;
            case "Processing":
                this.orderState = new ProcessingOrderState();
                break;
            case "Shipping":
                this.orderState = new ShippingOrderState();
                break;
            case "Delivered":
                this.orderState = new DeliveredOrderState();
                break;
            default:
                throw new IllegalStateException("Unknown order state: " + order.getStatus());
        }
    }

    public void handleStateChange(Order order){
        orderState.handleStateChange(order);
    }

    public void setOrderState(OrderState orderState){
        this.orderState = orderState;
    }
}
