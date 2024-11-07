package com.group.marketapp.coupon.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCouponRequestDto {

    private Long id;
    private String name;
    private LocalDateTime expiredDate;
    private boolean isActive;
    private int minimumMoney;
    private int discountPrice;

}
