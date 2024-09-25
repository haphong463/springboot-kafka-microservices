package net.javaguides.email_service.service;

import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.email_service.dto.ProductStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductAPIClient {
    @GetMapping("api/v1/products/{id}")
    ResponseEntity<ApiResponse<ProductStockResponse>> getProductById(@RequestParam("id") String id);
}
