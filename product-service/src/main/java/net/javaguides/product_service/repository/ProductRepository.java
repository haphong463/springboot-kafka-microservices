package net.javaguides.product_service.repository;

import net.javaguides.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByIdIn(Set<String> productIds);

    @Override
    Page<Product> findAll(Pageable pageable);
}
