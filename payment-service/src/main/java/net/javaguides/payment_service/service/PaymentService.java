package net.javaguides.payment_service.service;


import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.payment_service.dto.PaymentDto;
import net.javaguides.payment_service.entity.Payment;

public interface PaymentService {
    void createPayment(OrderEvent orderEvent);
    PaymentDto getPaymentByOrderId(String orderId);
}
