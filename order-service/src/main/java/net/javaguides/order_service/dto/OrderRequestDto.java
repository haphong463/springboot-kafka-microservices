package net.javaguides.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.common_lib.dto.order.OrderItemDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String orderId;
    private String status;
    private List<OrderItemDTO> orderItems;
    private String paymentMethod;
}
