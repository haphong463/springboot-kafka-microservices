package net.javaguides.stock_service.controller;


import io.github.haphong463.dto.ApiResponse;
import net.javaguides.stock_service.dto.StockUpdateRequest;
import net.javaguides.stock_service.entity.Stock;
import net.javaguides.stock_service.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getProductsStock(@RequestParam Set<String> productIds) {
        try {
            return new ResponseEntity<>(stockService.getProductsStock(productIds), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<?>> updateStockQuantity(
            @PathVariable("productId") String productId,
            @RequestBody StockUpdateRequest request
    ){
        try {
            Stock stock = stockService.updateStockQuantity(productId, request.getQuantity());
            if(stock != null){
                ApiResponse<Stock> apiResponse = new ApiResponse<>(stock, HttpStatus.OK.value());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            }
            ApiResponse<String> apiResponse = new ApiResponse<>("Product not found!", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }catch(Exception e){
            ApiResponse<String> apiResponse = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
