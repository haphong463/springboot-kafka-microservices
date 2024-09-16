package net.javaguides.identity_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.identity_service.enums.ERole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private ERole authority;
}