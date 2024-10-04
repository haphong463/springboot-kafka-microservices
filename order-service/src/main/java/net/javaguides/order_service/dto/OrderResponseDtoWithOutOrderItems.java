package net.javaguides.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDtoWithOutOrderItems {
    private OrderWithOutOrderItems orderDTO;
    private PaymentDto paymentDto;
}
