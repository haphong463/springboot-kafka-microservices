package net.javaguides.product_service.repository;

import net.javaguides.product_service.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByName(String name);
}
