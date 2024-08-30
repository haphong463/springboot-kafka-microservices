package net.javaguides.base_domains.dto.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
