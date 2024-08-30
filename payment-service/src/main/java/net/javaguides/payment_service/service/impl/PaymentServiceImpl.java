package net.javaguides.payment_service.service.impl;

import net.javaguides.base_domains.dto.order.OrderEvent;
import net.javaguides.payment_service.entity.Payment;
import net.javaguides.payment_service.repository.PaymentRepository;
import net.javaguides.payment_service.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    @Override
    public void createPayment(OrderEvent orderEvent) {
        Payment newPayment = new Payment();
        newPayment.setId(UUID.randomUUID().toString());
        BigDecimal amount = BigDecimal.valueOf(orderEvent.getOrderDTO().getQty() * orderEvent.getOrderDTO().getPrice());
        newPayment.setAmount(amount);
        newPayment.setPaymentMethod("Paypal");
        newPayment.setOrderId(orderEvent.getOrderDTO().getOrderId());
        newPayment.setStatus("Unpaid");

        paymentRepository.save(newPayment);
    }
}
