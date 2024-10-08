package net.javaguides.product_service.controller;


import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.product_service.dto.CreateProductRequestDto;
import net.javaguides.product_service.dto.ProductStockResponse;
import net.javaguides.product_service.dto.ProductUpdateDto;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ApiResponse<?>> saveProduct(@ModelAttribute @Valid CreateProductRequestDto createProductRequestDto) {
        try {
            ProductStockResponse createdProductDto = productService.saveProduct(createProductRequestDto);
            ApiResponse<ProductStockResponse> apiResponse = new ApiResponse<>(createdProductDto, HttpStatus.CREATED.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getProductList(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<ProductStockResponse> productList = productService.getProductList(page, size);
            ApiResponse<Page<ProductStockResponse>> apiResponse = new ApiResponse<>(productList, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable("id") String id) {
        try {
            ProductStockResponse productStockResponse = productService.getProductById(id);
            ApiResponse<ProductStockResponse> apiResponse = new ApiResponse<>(productStockResponse, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ProductException e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), e.getStatus().value());
            return new ResponseEntity<>(response, e.getStatus());
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable("id") String id, @RequestBody ProductUpdateDto productDTO, @RequestHeader(HttpHeaders.IF_MATCH) int version) {
        try {
            ProductStockResponse productStockResponse = productService.updateProduct(id, productDTO, version);
            ApiResponse<ProductStockResponse> apiResponse = new ApiResponse<>(productStockResponse, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        catch(OptimisticLockException e){
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.CONFLICT.value());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable("id") String id) {
        try {
            ProductDTO productDTO = productService.deleteProduct(id);
            if (productDTO != null) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            ApiResponse<String> response = new ApiResponse<>("Product not found with ID: " + id, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (ProductException e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), e.getStatus().value());
            return new ResponseEntity<>(response, e.getStatus());
        }
        catch (Exception e) {
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
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
