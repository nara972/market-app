package com.group.marketapp.product.repository;

import com.group.marketapp.product.doamin.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Modifying
    @Transactional
    @Query("update Product p set p.isDeleted =:isDeleted where p.id=:id")
    int updateStateProduct(@Param("isDeleted") boolean isDeleted, @Param("id") Long id);

}
