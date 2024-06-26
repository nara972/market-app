package com.group.marketapp.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.service.annotation.GetExchange;

@Data
public class CreateProductRequestDto {

    private String name;

    private int price;

    private int stock;

    private boolean isDeleted;

    @JsonProperty("product_category_id")
    private Long categoryId;

}
