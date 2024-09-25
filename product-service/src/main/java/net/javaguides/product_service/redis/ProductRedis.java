package net.javaguides.product_service.redis;

import net.javaguides.product_service.entity.Product;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRedis {
    private static final String HASH_KEY = "Product";

    @Autowired
    private RedisTemplate redisTemplate;

    public void save(Product productDTO){
        try {
            if(productDTO.getImageUrl() != null){
                redisTemplate.opsForHash().put(HASH_KEY, productDTO.getId(), productDTO);
            }
        }catch(Exception e){
            throw new RuntimeException("Error to save product in redis: " + e.getMessage());
        }
    }

    public Product findByProductId(String id){
        try {
            return (Product) redisTemplate.opsForHash().get(HASH_KEY, id);
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
