package net.javaguides.order_service.service;

import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.order_service.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentAPIClient {
    @GetMapping("api/v1/payment/{orderId}")
    ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrderId(@PathVariable("orderId") String orderId);
}
