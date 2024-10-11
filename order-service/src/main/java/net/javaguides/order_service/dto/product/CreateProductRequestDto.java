package net.javaguides.order_service.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {
    @NotBlank(message = "Name is required!")
    private String name;

    @NotBlank(message = "Description is required!")
    @Size(min = 10, message = "This description too short.")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull
    @Min(value = 1, message = "Stock quantity must be greater than 0")
    private Integer stockQuantity;
    private MultipartFile multipartFile;
}