package com.group.marketapp.repository;

import com.group.marketapp.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT c FROM ProductCategory c WHERE c = :parent OR c.parent =:parent")
    List<ProductCategory> findByCategoryAndSubCategories(@Param("parent") ProductCategory parent);

}
