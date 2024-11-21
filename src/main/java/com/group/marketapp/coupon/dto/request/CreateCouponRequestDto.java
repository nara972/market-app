package com.group.marketapp.coupon.dto.request;

import com.group.marketapp.coupon.domain.CouponType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCouponRequestDto {

    private String name;
    private CouponType couponType;
    private Integer quantity;
    private LocalDateTime expiredDate;
    private int minimumMoney;
    private int discountPrice;

}
