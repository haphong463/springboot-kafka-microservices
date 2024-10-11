package net.javaguides.order_service.dto.attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAttributeRequestDto {
    private String name;
    private String dataType;
}
