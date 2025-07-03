package com.group.marketapp.domain;

import com.group.marketapp.dto.requestdto.UpdateProductRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String name;

    @Column(length = 1000)
    private String content;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id", nullable = false)
    private ProductCategory productCategory;

    public Product() {}

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Builder
    public Product(Long id, String name, String content, int price, int stock, boolean isDeleted, ProductCategory productCategory) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.price = price;
        this.stock = stock;
        this.isDeleted = isDeleted;
        this.productCategory = productCategory;
    }

    public void update(UpdateProductRequestDto requestDto, ProductCategory productCategory) {
        this.name = requestDto.getName();
        this.content = requestDto.getContent();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
        this.productCategory = productCategory;
    }
}