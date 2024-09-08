package net.javaguides.product_service.controller;



import io.github.haphong463.dto.ApiResponse;
import io.github.haphong463.dto.product.ProductDTO;
import net.javaguides.product_service.dto.ProductStockResponse;
import net.javaguides.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> saveProduct(@RequestBody ProductDTO productDTO){
        try {
            ProductDTO createdProductDto = productService.saveProduct(productDTO);
            ApiResponse<ProductDTO> apiResponse = new ApiResponse<>(createdProductDto, HttpStatus.CREATED.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }catch(Exception e){
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProductList(){
        try {
            List<ProductStockResponse> productList = productService.getProductList();
            ApiResponse<List<ProductStockResponse>> apiResponse = new ApiResponse<>(productList, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(Exception e){
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@RequestParam("id") String id){
        try {
            ProductDTO productDTO = productService.getProductById(id);
            ApiResponse<ProductDTO> apiResponse = new ApiResponse<>(productDTO, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(Exception e){
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getProductsByIds(@RequestParam("ids") Set<String> productIds) {
        try {
            List<ProductDTO> productDTOs = productService.getProductsByIds(productIds);
            ApiResponse<List<ProductDTO>> apiResponse = new ApiResponse<>(productDTOs, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(Exception e){
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
