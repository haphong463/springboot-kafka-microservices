package net.javaguides.product_service.repository;

import net.javaguides.product_service.entity.Product;
import net.javaguides.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByIdIn(Set<Long> variantIds);
    List<ProductVariant> findByProductId(String productId);
}