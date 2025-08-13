package com.group.marketapp.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group.marketapp.domain.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseDto {

    private Long id;
    private String name;
    private String content;
    private int price;
    private int stock;
    private boolean isDeleted;
    private Long categoryId;
    private String categoryName;

    @Builder
    public ProductResponseDto(Long id, String name, String content, int price, int stock, boolean isDeleted, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.content = content;
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
                product.getContent(),
                product.getPrice(),
                product.getStock(),
                product.isDeleted(),
                product.getProductCategory().getId(),
                product.getProductCategory().getName()
        );
    }

}
