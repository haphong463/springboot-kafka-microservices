package net.javaguides.payment_service.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.order.OrderEvent;
import net.javaguides.common_lib.dto.order.OrderItemDTO;
import net.javaguides.payment_service.dto.PaymentDto;
import net.javaguides.payment_service.entity.Payment;
import net.javaguides.payment_service.entity.PaymentStatus;
import net.javaguides.payment_service.redis.PaymentRedis;
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
    private final PaymentRedis paymentRedis;


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
        newPayment.setPaymentMethod(orderEvent.getPaymentMethod());
        newPayment.setOrderId(orderEvent.getOrderDTO().getOrderId());
        newPayment.setStatus(PaymentStatus.PENDING);
        paymentRedis.save(newPayment);
        paymentRepository.save(newPayment);
    }

    @Override
    public PaymentDto getPaymentByOrderId(String orderId) {
        Payment cachePayment = paymentRedis.findByOrderId(orderId);

        if(cachePayment != null){
            return modelMapper.map(cachePayment, PaymentDto.class);
        }

        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if(existingPayment != null){
            paymentRedis.save(existingPayment);
            return modelMapper.map(existingPayment, PaymentDto.class);
        }
        return null;
    }



    @Override
    public void updateStatusPayment(String orderId, PaymentStatus status) {
        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if(existingPayment != null){
            existingPayment.setStatus(status);
            paymentRedis.save(existingPayment);
            paymentRepository.save(existingPayment);
        }
        System.out.println("Khong tim thay payment!");
    }

    @Override
    public void refundPayment(String orderId) {
        Payment existingPayment = paymentRepository.findByOrderId(orderId);
        if(existingPayment != null){
            existingPayment.setStatus(PaymentStatus.REFUND);
            paymentRepository.save(existingPayment);
        }
        System.out.println("Khong tim thay payment!");
    }
}
