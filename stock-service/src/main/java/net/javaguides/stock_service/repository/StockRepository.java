package net.javaguides.stock_service.repository;

import net.javaguides.stock_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(String productId);
    List<Stock> findAllByProductIdIn(Set<String> productIds);
}
