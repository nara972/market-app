package com.group.marketapp.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateProductRequestDto {

    private String name;

    private int price;

    private int stock;

    private boolean isDeleted;

    @JsonProperty("product_category_id")
    private Long categoryId;

}
