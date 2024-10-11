package net.javaguides.product_service.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.product_service.dto.product_variant.CreateProductVariantRequestDto;
import net.javaguides.product_service.dto.product_variant.ProductVariantResponseDto;
import net.javaguides.product_service.dto.product_variant.UpdateProductVariantRequestDto;
import net.javaguides.product_service.entity.ProductVariant;
import net.javaguides.product_service.service.ProductVariantService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    private final ModelMapper modelMapper;

    /**
     * Thêm một biến thể mới cho sản phẩm
     */
    @PostMapping("{productId}")
    public ResponseEntity<ProductVariantResponseDto> createVariant(
            @PathVariable String productId,
            @RequestBody CreateProductVariantRequestDto requestDto) {
        ProductVariantResponseDto variant = productVariantService.createProductVariant(productId, requestDto.getAttributes(), requestDto.getPrice(), requestDto.getSku(), requestDto.getInitialStock(), requestDto.getReorderLevel());

        return ResponseEntity.ok(variant);
    }

    /**
     * Lấy tất cả biến thể của một sản phẩm
     */
    @GetMapping("{productId}")
    public ResponseEntity<List<ProductVariantResponseDto>> getVariants(@PathVariable String productId) {
        List<ProductVariantResponseDto> variants = productVariantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantResponseDto>> getVariantsByIds(@RequestParam("variantIds") Set<Long> variantIds) {
        List<ProductVariant> variants = productVariantService.getProductVariantByIds(variantIds);
        return ResponseEntity.ok(variants.stream().map(productVariant -> modelMapper.map(productVariant,ProductVariantResponseDto.class)).collect(Collectors.toList()));
    }



    @PutMapping("{variantId}")
    public ResponseEntity<ApiResponse<?>> updateVariant(@PathVariable Long variantId, @RequestBody UpdateProductVariantRequestDto requestDto){
        try {
            ProductVariantResponseDto responseDto = productVariantService.updateProductVariant(variantId, requestDto);
            return new ResponseEntity<>(new ApiResponse<>(responseDto, HttpStatus.OK.value()), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{variantId}")
    public ResponseEntity<ApiResponse<?>> deleteVariant(@PathVariable Long variantId){
        try {
            productVariantService.deleteProductVariant(variantId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }catch(Exception e){
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Các API khác như update, delete nếu cần
}
