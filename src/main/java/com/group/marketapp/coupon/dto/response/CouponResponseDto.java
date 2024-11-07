package com.group.marketapp.coupon.dto.response;

import com.group.marketapp.coupon.domain.Coupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponResponseDto {

    private Long id;
    private String name;
    private LocalDateTime expiredDate;
    private boolean isActive;
    private int minimumMoney;
    private int discountPrice;

    public CouponResponseDto(Long id, String name, LocalDateTime expiredDate, boolean isActive, int minimumMoney, int discountPrice) {
        this.id = id;
        this.name = name;
        this.expiredDate = expiredDate;
        this.isActive = isActive;
        this.minimumMoney = minimumMoney;
        this.discountPrice = discountPrice;
    }

    public static CouponResponseDto of(Coupon coupon) {
        return new CouponResponseDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getExpiredDate(),
                coupon.isActive(),
                coupon.getMinimumMoney(),
                coupon.getDiscountPrice()
        );
    }

}
