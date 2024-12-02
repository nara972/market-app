package com.group.marketapp.dto.responsedto;

import com.group.marketapp.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private int stock;
    private boolean isDeleted;
    private Long categoryId;
    private String categoryName;

    @Builder
    public ProductResponseDto(Long id, String name, int price, int stock, boolean isDeleted, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.isDeleted = isDeleted;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public static ProductResponseDto of(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.isDeleted(),
                product.getProductCategory().getId(),
                product.getProductCategory().getName()
        );
    }

}
