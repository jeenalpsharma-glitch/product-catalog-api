package com.example.product_catalog_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.product_catalog_api.entity.Product;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);
}
