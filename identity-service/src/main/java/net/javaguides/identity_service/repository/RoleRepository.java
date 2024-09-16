package net.javaguides.identity_service.repository;

import net.javaguides.identity_service.entity.Role;
import net.javaguides.identity_service.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}