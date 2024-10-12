package net.javaguides.product_service.redis;

import net.javaguides.product_service.dto.product.ProductCacheDto;
import net.javaguides.product_service.entity.Product;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class ProductRedis {
    private static final String HASH_KEY = "Product";

    @Autowired
    private RedisTemplate<String, ProductCacheDto> redisTemplate;

    @Autowired
    private ModelMapper modelMapper;

    public void save(Product product){
        try {
            if(product.getImageUrl() != null){
                ProductCacheDto productCacheDto = modelMapper.map(product, ProductCacheDto.class);

                redisTemplate.opsForHash().put(HASH_KEY, product.getId(), productCacheDto);
                redisTemplate.expire(HASH_KEY, Duration.ofHours(1));
            }
        }catch(Exception e){
            throw new RuntimeException("Error to save product in redis: " + e.getMessage());
        }
    }

    public ProductCacheDto findByProductId(String id){
        try {
            return (ProductCacheDto) redisTemplate.opsForHash().get(HASH_KEY, id);
        }catch(Exception e){
            return null;
        }
    }

    public void deleteByProductId(String id){
        try {
            redisTemplate.opsForHash().delete(HASH_KEY, id);
        }catch(Exception e){
            throw new ResourceNotFoundException("Not Found", e);
        }
    }

}
