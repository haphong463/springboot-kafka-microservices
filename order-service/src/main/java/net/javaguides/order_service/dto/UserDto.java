package net.javaguides.order_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
