package com.group.marketapp.product.repository;

import com.group.marketapp.product.doamin.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
