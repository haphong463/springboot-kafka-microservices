package net.javaguides.base_domains.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderId;
    private String status;
    private List<OrderItemDTO> orderItems;
}
