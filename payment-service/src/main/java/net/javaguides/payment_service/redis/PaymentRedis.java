package net.javaguides.payment_service.redis;

import net.javaguides.payment_service.entity.Payment;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class PaymentRedis {
    private static final String HASH_KEY = "Payment";
    @Autowired
    private RedisTemplate redisTemplate;

    public void save(Payment payment) {
        try {
            redisTemplate.opsForHash().put(HASH_KEY, payment.getOrderId(), payment);
            redisTemplate.expire(HASH_KEY, Duration.ofHours(1));

        } catch (Exception e) {
            throw new RuntimeException("Error saving payment in Redis: " + e.getMessage(), e);
        }
    }

    public Payment findByOrderId(String orderId) {
        try {
            return (Payment) redisTemplate.opsForHash().get(HASH_KEY, orderId);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteByOrderId(String id) {
        try {
            redisTemplate.opsForHash().delete(HASH_KEY, id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Not Found", e);
        }
    }
}
