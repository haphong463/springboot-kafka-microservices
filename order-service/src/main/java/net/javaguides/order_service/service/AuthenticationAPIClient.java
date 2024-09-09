package net.javaguides.order_service.service;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.order_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "IDENTITY-SERVICE")
public interface AuthenticationAPIClient {
    @GetMapping("api/v1/auth/me")
    ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@RequestHeader(HttpHeaders.COOKIE) String cookie);
}
