package net.javaguides.product_service.service;

import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.product_service.dto.StockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "STOCK-SERVICE")
public interface StockAPIClient {
    @GetMapping("api/v1/stock")
    ResponseEntity<List<StockResponseDto>> getProductsStock(@RequestParam("productIds") Set<String> productIds);

    @GetMapping("api/v1/stock/{productId}")
    ResponseEntity<ApiResponse<StockResponseDto>> getProductStock(@PathVariable("productId") String productId);
}
