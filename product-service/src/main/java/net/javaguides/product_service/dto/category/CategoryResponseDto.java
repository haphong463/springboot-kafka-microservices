package net.javaguides.product_service.dto.category;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponseDto {
    private String id;
    private String name;
    private String parentId;
    private List<CategoryResponseDto> children = new ArrayList<>();
}