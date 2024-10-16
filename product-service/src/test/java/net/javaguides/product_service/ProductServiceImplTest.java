package net.javaguides.product_service;

import net.javaguides.product_service.dto.product.CreateProductRequestDto;
import net.javaguides.product_service.dto.product.ProductResponseDto;
import net.javaguides.product_service.dto.product.ProductCacheDto;
import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.repository.ProductRepository;
import net.javaguides.product_service.service.CloudinaryService;
import net.javaguides.product_service.redis.ProductRedis;
import net.javaguides.product_service.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductRedis productDAO;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private CreateProductRequestDto createProductRequestDto;
    private Product product;
    private ProductCacheDto productCacheDto;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void setUp() {
        // Khởi tạo đối tượng CreateProductRequestDto
        createProductRequestDto = new CreateProductRequestDto();
        createProductRequestDto.setName("Test Product");
        createProductRequestDto.setDescription("Test Description");
        createProductRequestDto.setPrice(new BigDecimal("100.00"));
        createProductRequestDto.setMultipartFile(mock(MultipartFile.class));

        // Khởi tạo đối tượng Product
        product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName(createProductRequestDto.getName());
        product.setDescription(createProductRequestDto.getDescription());
        product.setPrice(createProductRequestDto.getPrice());
        product.setImageUrl("http://test.com/image.jpg");

        // Khởi tạo đối tượng ProductCacheDto
        productCacheDto = new ProductCacheDto();
        productCacheDto.setId(product.getId());
        productCacheDto.setName(product.getName());
        productCacheDto.setDescription(product.getDescription());
        productCacheDto.setPrice(product.getPrice());
        productCacheDto.setImageUrl(product.getImageUrl());

        // Khởi tạo đối tượng ProductResponseDto
        productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setName(product.getName());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setImageUrl(product.getImageUrl());
    }

    @Test
    void testSaveProduct_Success() throws Exception {
        // Mock behavior
        when(modelMapper.map(any(CreateProductRequestDto.class), eq(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponseDto.class))).thenReturn(productResponseDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        doNothing().when(productDAO).save(any(Product.class));
        doNothing().when(cloudinaryService).uploadFile(any(MultipartFile.class), anyString());
        when(createProductRequestDto.getMultipartFile().getOriginalFilename()).thenReturn("image.jpg");

        // Thực thi phương thức cần test
        ProductResponseDto responseDto = productService.saveProduct(createProductRequestDto);

        // Kiểm tra kết quả
        assertNotNull(responseDto);
        assertEquals(product.getName(), responseDto.getName());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productDAO, times(1)).save(any(Product.class));
        verify(cloudinaryService, times(1)).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    void testSaveProduct_Exception() throws Exception {
        // Mock behavior để ném ra ngoại lệ khi mapping
        when(modelMapper.map(any(CreateProductRequestDto.class), eq(Product.class))).thenThrow(new RuntimeException("Mapping error"));

        // Thực thi và kiểm tra ngoại lệ
        ProductException exception = assertThrows(ProductException.class, () -> {
            productService.saveProduct(createProductRequestDto);
        });

        assertTrue(exception.getMessage().contains("Failed to create product"));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testGetProductById_ProductExistsInCache() {
        // Mock behavior
        when(productDAO.findByProductId(anyString())).thenReturn(productCacheDto);
        when(modelMapper.map(any(ProductCacheDto.class), eq(ProductResponseDto.class))).thenReturn(productResponseDto);

        // Thực thi
        ProductResponseDto responseDto = productService.getProductById(product.getId());

        // Kiểm tra kết quả
        assertNotNull(responseDto);
        verify(productDAO, times(1)).findByProductId(product.getId());
        verify(productRepository, never()).findById(anyString());
    }

    @Test
    void testGetProductById_ProductNotInCacheButExistsInDB() {
        // Mock behavior
        when(productDAO.findByProductId(anyString())).thenReturn(null);
        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
        when(modelMapper.map(any(Product.class), eq(ProductResponseDto.class))).thenReturn(productResponseDto);
        doNothing().when(productDAO).save(any(Product.class));

        // Thực thi
        ProductResponseDto responseDto = productService.getProductById(product.getId());

        // Kiểm tra kết quả
        assertNotNull(responseDto);
        verify(productDAO, times(1)).findByProductId(product.getId());
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void testGetProductById_ProductNotFound() {
        // Mock behavior
        when(productDAO.findByProductId(anyString())).thenReturn(null);
        when(productRepository.findById(anyString())).thenReturn(Optional.empty());

        // Thực thi và kiểm tra ngoại lệ
        ProductException exception = assertThrows(ProductException.class, () -> {
            productService.getProductById("non-existing-id");
        });

        assertTrue(exception.getMessage().contains("Product not found with id"));

        verify(productDAO, times(1)).findByProductId("non-existing-id");
        verify(productRepository, times(1)).findById("non-existing-id");
    }
}
