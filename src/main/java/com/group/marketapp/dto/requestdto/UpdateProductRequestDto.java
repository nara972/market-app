package com.group.marketapp.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateProductRequestDto {

    private Long id;

    private String name;

    private int price;

    private int stock;

    @JsonProperty("product_category_id")
    private Long categoryId;

}
