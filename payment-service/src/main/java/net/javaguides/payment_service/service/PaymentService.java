package net.javaguides.payment_service.service;


import io.github.haphong463.dto.order.OrderEvent;

public interface PaymentService {
    void createPayment(OrderEvent orderEvent);
}
