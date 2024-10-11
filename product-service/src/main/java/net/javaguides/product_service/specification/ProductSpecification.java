package net.javaguides.product_service.specification;

import net.javaguides.product_service.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> inCategory(String categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("categories").get("id"), categoryId);
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }

    // Thêm các tiêu chí khác nếu cần
}
