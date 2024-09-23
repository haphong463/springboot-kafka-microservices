package net.javaguides.product_service.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.common_lib.dto.product.ProductDTO;
import net.javaguides.common_lib.dto.product.ProductEvent;
import net.javaguides.common_lib.dto.product.ProductMethod;
import net.javaguides.product_service.redis.ProductRedis;
import net.javaguides.product_service.dto.ProductResponseDto;
import net.javaguides.product_service.dto.ProductStockResponse;
import net.javaguides.product_service.dto.ProductUpdateDto;
import net.javaguides.product_service.dto.StockResponseDto;
import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.kafka.ProductProducer;
import net.javaguides.product_service.repository.ProductRepository;
import net.javaguides.product_service.service.ProductService;
import net.javaguides.product_service.service.StockAPIClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductProducer productProducer;
    private final ProductRepository productRepository;
    private final StockAPIClient stockAPIClient;
    private final ModelMapper modelMapper;
    private final ProductRedis productDAO;

    @Override
    public ProductStockResponse saveProduct(ProductDTO productDTO) {
        try {
            Product product = mapToEntity(productDTO);
            product.setId(UUID.randomUUID().toString());

            Product savedProduct = productRepository.save(product);
            ProductEvent productEvent = createProductEvent(savedProduct, productDTO.getStockQuantity(), ProductMethod.CREATE);
            productProducer.sendMessage(productEvent);
            return buildProductStockResponse(savedProduct, productDTO.getStockQuantity());
        } catch (Exception e) {
            throw new ProductException("Failed to create product: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ProductStockResponse getProductById(String id) {
        Product existingProductInCache = getExistingProductInCache(id);
        ProductResponseDto productResponseDto;

        if (existingProductInCache != null) {
            LOGGER.info("ProductServiceImpl.getProductById(): cache post >> " + existingProductInCache.toString());
            productResponseDto = modelMapper.map(existingProductInCache, ProductResponseDto.class);
        } else {


            // Cache miss, retrieve product from database
            Optional<Product> productOpt = productRepository.findById(id);
            if (!productOpt.isPresent()) {
                throw new ProductException("Not found product!", HttpStatus.NOT_FOUND);
            }
            Product product = productOpt.get();
            productResponseDto = modelMapper.map(product, ProductResponseDto.class);

            // Store product data in Redis cache
            insertProductToCache(product);
            // Optionally set a time to live for the cache
        }

        // Always retrieve stock information fresh from the Stock API client
        ApiResponse<StockResponseDto> stockApiResponse = stockAPIClient.getProductStock(id).getBody();

        // Build the response
        ProductStockResponse productStockResponse = new ProductStockResponse();
        productStockResponse.setStock(stockApiResponse.getData());
        productStockResponse.setProduct(productResponseDto);
        return productStockResponse;
    }



    @Override
    public List<ProductStockResponse> getProductList() {
        List<ProductResponseDto> productDtos = productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .toList();

        Set<String> productIds = productDtos.stream()
                .map(ProductResponseDto::getId)
                .collect(Collectors.toSet());

        List<StockResponseDto> stockList = stockAPIClient.getProductsStock(productIds).getBody();

        return productDtos.stream()
                .map(productDto -> {
                    StockResponseDto stock = stockList.stream()
                            .filter(s -> s.getProductId().equals(productDto.getId()))
                            .findFirst()
                            .orElse(new StockResponseDto());
                    return buildProductStockResponse(productDto, stock);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductStockResponse updateProduct(String id, ProductDTO productDTO, int version) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    if (existingProduct.getVersion() != version) {
                        throw new ProductException("Version conflict! Current version: "
                                + existingProduct.getVersion(), HttpStatus.CONFLICT);
                    }
                    return updateAndSaveProduct(existingProduct, productDTO);
                })
                .orElseThrow(() -> new ProductException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
    }


    @Override
    public ProductDTO deleteProduct(String id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    ProductEvent productEvent = createProductEvent(productDTO, ProductMethod.DELETE);
                    productProducer.sendDeleteProductMessage(productEvent);
                    productRepository.delete(product);
                    productDAO.deleteByProductId(id);
                    return productDTO;
                }).orElseThrow(() -> new ProductException("Product not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public List<ProductDTO> getProductsByIds(Set<String> productIds) {
        return productRepository.findAllByIdIn(productIds)
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    // Private Helper Methods
    private Product mapToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private ProductEvent createProductEvent(Product product, int stockQuantity, ProductMethod method) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        productDTO.setStockQuantity(stockQuantity);

        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductDTO(productDTO);
        productEvent.setMethod(method);
        return productEvent;
    }

    private ProductEvent createProductEvent(ProductDTO productDTO, ProductMethod method) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductDTO(productDTO);
        productEvent.setMethod(method);
        return productEvent;
    }

    private ProductStockResponse buildProductStockResponse(Product product, int stockQuantity) {
        ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
        StockResponseDto stockResponseDto = new StockResponseDto(product.getId(), stockQuantity);

        ProductStockResponse productStockResponse = new ProductStockResponse();
        productStockResponse.setProduct(productResponseDto);
        productStockResponse.setStock(stockResponseDto);
        return productStockResponse;
    }

    private ProductStockResponse buildProductStockResponse(ProductResponseDto productDto, StockResponseDto stockDto) {
        ProductStockResponse response = new ProductStockResponse();
        response.setProduct(productDto);
        response.setStock(stockDto);
        return response;
    }

    private ProductStockResponse updateAndSaveProduct(Product existingProduct, ProductDTO productDTO) {
        ProductUpdateDto productUpdateDto = modelMapper.map(productDTO, ProductUpdateDto.class);
        modelMapper.map(productUpdateDto, existingProduct);

        Product savedProduct = productRepository.save(existingProduct);
        ProductEvent productEvent = createProductEvent(savedProduct, productDTO.getStockQuantity(), ProductMethod.UPDATE);
        productProducer.sendMessage(productEvent);

        return buildProductStockResponse(savedProduct, productDTO.getStockQuantity());
    }

    private Product getExistingProductInCache(String id) {
        return productDAO.findByProductId(id);
    }

    private void insertProductToCache(Product product) {
        productDAO.save(product);
        LOGGER.info("ProductServiceImpl.getProductById(): cache insert >> " + product.getId());
    }

}
