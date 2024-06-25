package com.group.marketapp.product.repository;

import com.group.marketapp.product.doamin.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
