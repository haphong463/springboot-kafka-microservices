package net.javaguides.order_service.service.state;

import net.javaguides.order_service.entity.Order;

public class OrderContext {
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
