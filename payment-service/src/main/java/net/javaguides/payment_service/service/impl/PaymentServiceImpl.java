package net.javaguides.payment_service.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.payment_service.dto.PaymentDto;
import net.javaguides.payment_service.entity.Payment;
import net.javaguides.payment_service.entity.PaymentStatus;
import net.javaguides.payment_service.repository.PaymentRepository;
import net.javaguides.payment_service.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;


    @Override
    public void createPayment(OrderEvent orderEvent) {
        Payment newPayment = new Payment();
        newPayment.setId(UUID.randomUUID().toString());

        BigDecimal amount = BigDecimal.valueOf(0);

        for (OrderItemDTO orderItemDTO : orderEvent.getOrderDTO().getOrderItems()) {
            BigDecimal itemTotal = orderItemDTO.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.getQuantity()));
            amount = amount.add(itemTotal);
        }

        newPayment.setAmount(amount);
        newPayment.setPaymentMethod("Paypal");
        newPayment.setOrderId(orderEvent.getOrderDTO().getOrderId());
        newPayment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(newPayment);
    }

    @Override
    public PaymentDto getPaymentByOrderId(String orderId) {
        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if(existingPayment != null){

            return modelMapper.map(existingPayment, PaymentDto.class);
        }
        return null;
    }
}
