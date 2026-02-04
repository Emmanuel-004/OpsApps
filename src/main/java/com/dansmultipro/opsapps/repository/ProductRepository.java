package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Boolean existsByName(String name);
    Boolean existsByCode(String code);
}
