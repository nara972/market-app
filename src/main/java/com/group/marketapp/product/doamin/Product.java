package com.group.marketapp.product.doamin;

import com.group.marketapp.product.dto.request.UpdateProductRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private int stock;

    private boolean isDeleted;

    public Product(){

    }

    @Builder
    public Product(Long id, String name, int price, int stock, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.isDeleted = isDeleted;
    }

    public void update(UpdateProductRequestDto requestDto){
        this.name = requestDto.getName();
        this.price = requestDto.getPrice();
        this.stock = requestDto.getStock();
    }

}
