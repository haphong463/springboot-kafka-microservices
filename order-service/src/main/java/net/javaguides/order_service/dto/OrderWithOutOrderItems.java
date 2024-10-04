package net.javaguides.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithOutOrderItems {
    private String orderId;
    private String status;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
    private int version;
}
