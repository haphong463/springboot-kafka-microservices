package net.javaguides.order_service.service;

import io.github.haphong463.dto.ApiResponse;
import net.javaguides.order_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "IDENTITY-SERVICE")
public interface AuthenticationAPIClient {
    @GetMapping("api/v1/auth/me")
    ResponseEntity<ApiResponse<UserDto>> getCurrentUser();
}
