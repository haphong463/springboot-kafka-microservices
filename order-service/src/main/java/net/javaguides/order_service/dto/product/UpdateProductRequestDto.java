package net.javaguides.order_service.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDto {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer stockQuantity;
    private MultipartFile multipartFile;
}

