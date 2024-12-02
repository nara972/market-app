package com.group.marketapp.dto.requestdto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductRequestDto {

    private Long productId;

    private int count;

    @Builder
    public OrderProductRequestDto(Long productId, int count) {
        this.productId = productId;
        this.count = count;
    }

    public OrderProductRequestDto() {}
}
