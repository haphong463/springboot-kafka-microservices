package net.javaguides.product_service.controller;

import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.product_service.dto.category.CategoryResponseDto;
import net.javaguides.product_service.dto.category.CreateCategoryRequestDto;
import net.javaguides.product_service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(@RequestBody CreateCategoryRequestDto requestDto) {
        CategoryResponseDto category = categoryService.createCategory(requestDto);
        ApiResponse<CategoryResponseDto> response = new ApiResponse<>(category, HttpStatus.CREATED.value());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(@PathVariable String id, @RequestBody CreateCategoryRequestDto requestDto) {
        CategoryResponseDto category = categoryService.updateCategory(id, requestDto);
        ApiResponse<CategoryResponseDto> response = new ApiResponse<>(category, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.NO_CONTENT.value());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getCategoryById(@PathVariable String id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        ApiResponse<CategoryResponseDto> response = new ApiResponse<>(category, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        ApiResponse<List<CategoryResponseDto>> response = new ApiResponse<>(categories, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getRootCategories() {
        List<CategoryResponseDto> rootCategories = categoryService.getRootCategories();
        ApiResponse<List<CategoryResponseDto>> response = new ApiResponse<>(rootCategories, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
