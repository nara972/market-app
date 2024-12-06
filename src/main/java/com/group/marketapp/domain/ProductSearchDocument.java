package com.group.marketapp.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products")
@Getter
public class ProductSearchDocument {

    @Id
    private Long id;

    private String name;

    private String categoryName;

    private Long categoryId;

    private int price;

    private boolean isDeleted;

    @Builder
    public ProductSearchDocument(Long id, String name, String categoryName, Long categoryId,int price, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.categoryId=categoryId;
        this.price = price;
        this.isDeleted = isDeleted;
    }
}
