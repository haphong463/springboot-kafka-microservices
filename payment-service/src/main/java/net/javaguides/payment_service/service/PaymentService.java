package net.javaguides.payment_service.service;


import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.payment_service.dto.PaymentDto;
import net.javaguides.payment_service.entity.Payment;
import net.javaguides.payment_service.entity.PaymentStatus;

public interface PaymentService {
    void createPayment(OrderEvent orderEvent);
    PaymentDto getPaymentByOrderId(String orderId);
    void updateStatusPayment(String orderId, PaymentStatus status);
    void refundPayment(String orderId);
}
