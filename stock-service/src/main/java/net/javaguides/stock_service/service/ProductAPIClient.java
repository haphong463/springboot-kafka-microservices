package net.javaguides.stock_service.service;


import io.github.haphong463.dto.ApiResponse;
import io.github.haphong463.dto.product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductAPIClient {
    @GetMapping("api/v1/products/{id}")
    ResponseEntity<ApiResponse<ProductDTO>> getProductById(@RequestParam("id") String id);
}
