package com.group.marketapp.coupon.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCouponRequestDto {

    private String name;
    private LocalDateTime expiredDate;
    private int minimumMoney;
    private int discountPrice;

}
