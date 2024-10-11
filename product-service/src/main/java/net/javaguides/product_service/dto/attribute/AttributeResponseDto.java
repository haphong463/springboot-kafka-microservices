package net.javaguides.product_service.dto.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponseDto {
    private Long id;
    private String name;
}
