package net.javaguides.common_lib.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductEvent {
    private ProductDTO productDTO;
    private ProductMethod method;
}
