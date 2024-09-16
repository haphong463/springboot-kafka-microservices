package net.javaguides.common_lib.dto.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderId;
    private String status;
    private List<OrderItemDTO> orderItems;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
    private int version;
}
