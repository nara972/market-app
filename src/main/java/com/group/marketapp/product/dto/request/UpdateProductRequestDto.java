package com.group.marketapp.product.dto.request;

import lombok.Data;

@Data
public class UpdateProductRequestDto {

    private Long id;

    private String name;

    private int price;

    private int stock;


}
