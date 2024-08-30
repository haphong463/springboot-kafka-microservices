package net.javaguides.product_service.service.impl;

import net.javaguides.base_domains.dto.order.OrderDTO;
import net.javaguides.base_domains.dto.order.OrderEvent;
import net.javaguides.base_domains.dto.product.ProductDTO;
import net.javaguides.base_domains.dto.product.ProductEvent;
import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.kafka.ProductProducer;
import net.javaguides.product_service.repository.ProductRepository;
import net.javaguides.product_service.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductProducer productProducer;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public ProductServiceImpl(ProductProducer productProducer, ProductRepository productRepository, ModelMapper modelMapper) {
        this.productProducer = productProducer;
        this.productRepository = productRepository;
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

    private ProductEvent createProductEvent(Product createdProduct) {
        ProductDTO createdProductDto = modelMapper.map(createdProduct, ProductDTO.class);
        ProductEvent orderEvent = new ProductEvent();
        orderEvent.setProductDTO(createdProductDto);
        return orderEvent;
    }
}
