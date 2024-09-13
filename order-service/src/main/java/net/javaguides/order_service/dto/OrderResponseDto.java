package net.javaguides.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.common_lib.dto.order.OrderDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private OrderDTO orderDTO;
    private PaymentDto paymentDto;
}
