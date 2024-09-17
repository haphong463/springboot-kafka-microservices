package net.javaguides.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
}
