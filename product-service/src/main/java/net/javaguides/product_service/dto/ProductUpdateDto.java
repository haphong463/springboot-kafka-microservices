package net.javaguides.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDto {
    private String name;
    private double price;
    private String description;
    private String imageUrl;
}

