package net.javaguides.product_service.service.impl;

import net.javaguides.base_domains.dto.product.ProductDTO;
import net.javaguides.base_domains.dto.product.ProductEvent;
import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.exception.ProductException;
import net.javaguides.product_service.kafka.ProductProducer;
import net.javaguides.product_service.repository.ProductRepository;
import net.javaguides.product_service.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public ProductDTO getProductById(String id) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if(existingProduct != null){
            return modelMapper.map(existingProduct, ProductDTO.class);
        }
        return null;
    }

    @Override
    public List<ProductDTO> getProductList() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
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
