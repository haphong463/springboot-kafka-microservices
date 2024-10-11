package net.javaguides.product_service.dto.attribute_value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.product_service.dto.attribute.AttributeResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValueResponseDto {
    private AttributeResponseDto attribute;
    private String value;
}
