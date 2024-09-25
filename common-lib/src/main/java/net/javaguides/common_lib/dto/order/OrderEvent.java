package net.javaguides.common_lib.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String message;
    private String status;
    private OrderDTO orderDTO;
    private String paymentMethod;
    private String email;
}