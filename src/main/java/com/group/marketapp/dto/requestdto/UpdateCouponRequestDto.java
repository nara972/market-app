package com.group.marketapp.dto.requestdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCouponRequestDto {

    private Long id;
    private String name;
    private Integer quantity;
    private LocalDateTime expiredDate;
    private boolean isActive;
    private int minimumMoney;
    private int discountPrice;

}
