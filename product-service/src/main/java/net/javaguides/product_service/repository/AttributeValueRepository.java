package net.javaguides.product_service.repository;

import net.javaguides.product_service.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    List<AttributeValue> findByProductVariantId(Long productVariantId);
}
