package net.javaguides.product_service.service.impl;


import io.github.haphong463.dto.product.ProductDTO;
import io.github.haphong463.dto.product.ProductEvent;
import net.javaguides.product_service.dto.ProductStockResponse;
import net.javaguides.product_service.dto.StockDto;
import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.kafka.ProductProducer;
import net.javaguides.product_service.repository.ProductRepository;
import net.javaguides.product_service.service.ProductService;
import net.javaguides.product_service.service.StockAPIClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductProducer productProducer;
    private final ProductRepository productRepository;
    private final StockAPIClient stockAPIClient;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductProducer productProducer, ProductRepository productRepository, StockAPIClient stockAPIClient, ModelMapper modelMapper) {
        this.productProducer = productProducer;
        this.productRepository = productRepository;
        this.stockAPIClient = stockAPIClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        try {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setId(UUID.randomUUID().toString());
            Product savedProduct = productRepository.save(product);

            ProductEvent productEvent = createProductEvent(savedProduct);

            productProducer.sendMessage(productEvent);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }catch(Exception e){
            throw new ProductException("Failed to create product: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if(existingProduct != null){
            return modelMapper.map(existingProduct, ProductDTO.class);
        }
        return null;
    }

    @Override
    public List<ProductStockResponse> getProductList() {
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        Set<String> productIds = products
                .stream()
                .map(ProductDTO::getId)
                .collect(Collectors.toSet());

        List<StockDto> stockList = stockAPIClient.getProductsStock(productIds).getBody();

        return products.stream()
                .map(product -> {
                    StockDto stock = stockList.stream()
                            .filter(s -> s.getProductId().equals(product.getId()))
                            .findFirst()
                            .orElse(null);

                    ProductStockResponse response = new ProductStockResponse();
                    response.setProduct(product);
                    response.setStock(stock);
                    return response;
                })
                .collect(Collectors.toList());
    }


    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        return null;
    }

    @Override
    public ProductDTO deleteProduct(String id) {
        return null;
    }

    @Override
    public List<ProductDTO> getProductsByIds(Set<String> productIds) {
        return productRepository.findAllByIdIn(productIds).
                stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    private ProductEvent createProductEvent(Product createdProduct) {
        ProductDTO createdProductDto = modelMapper.map(createdProduct, ProductDTO.class);
        ProductEvent orderEvent = new ProductEvent();
        orderEvent.setProductDTO(createdProductDto);
        return orderEvent;
    }
}
