package net.javaguides.payment_service.service;


import net.javaguides.common_lib.dto.order.OrderEvent;

public interface PaymentService {
    void createPayment(OrderEvent orderEvent);
}
