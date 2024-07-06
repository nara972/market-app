package com.group.marketapp.product.repository;

import com.group.marketapp.product.domain.Product;
import com.group.marketapp.product.domain.ProductCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Modifying
    @Transactional
    @Query("update Product p set p.isDeleted =:isDeleted where p.id=:id")
    int updateStateProduct(@Param("isDeleted") boolean isDeleted, @Param("id") Long id);

    @Query("select p from Product p where p.id =:id and p.isDeleted = false")
    Optional<Product> findById(@Param("id") Long id);

    List<Product> findByProductCategory(ProductCategory productCategory);

}
