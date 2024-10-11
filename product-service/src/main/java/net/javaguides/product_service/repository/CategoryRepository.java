package net.javaguides.product_service.repository;

import net.javaguides.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Category findByName(String name);
    List<Category> findByParentIsNull(); // Lấy danh mục gốc
}
