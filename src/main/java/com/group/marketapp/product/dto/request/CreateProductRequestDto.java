package com.group.marketapp.product.dto.request;

import lombok.Data;
import org.springframework.web.service.annotation.GetExchange;

@Data
public class CreateProductRequestDto {

    private String name;

    private int price;

    private int stock;

    private boolean isDeleted;

}
