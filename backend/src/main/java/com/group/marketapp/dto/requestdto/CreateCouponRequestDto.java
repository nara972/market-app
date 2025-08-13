package com.group.marketapp.dto.requestdto;

import com.group.marketapp.domain.CouponType;
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
