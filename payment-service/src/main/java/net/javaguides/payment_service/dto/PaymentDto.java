package net.javaguides.payment_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.payment_service.entity.PaymentStatus;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
}
