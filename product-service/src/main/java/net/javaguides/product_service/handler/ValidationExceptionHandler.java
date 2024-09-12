package net.javaguides.product_service.handler;

import net.javaguides.common_lib.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Tạo ApiResponse với lỗi
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(errors, HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}