package com.group.marketapp.repository;

import com.group.marketapp.domain.Product;
import com.group.marketapp.domain.ProductCategory;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id =:id and p.isDeleted = false")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    List<Product> findByProductCategoryIn(List<ProductCategory> productCategory);

}
