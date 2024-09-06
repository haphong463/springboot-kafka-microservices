package net.javaguides.payment_service.service;


import net.javaguides.base_domains.dto.order.OrderEvent;

public interface PaymentService {
    void createPayment(OrderEvent orderEvent);
}
