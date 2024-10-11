package net.javaguides.product_service.dto.category;

import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    private String name;
    private String parentId; // Có thể null nếu là danh mục gốc
}