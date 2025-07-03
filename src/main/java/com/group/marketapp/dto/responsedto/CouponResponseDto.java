package com.group.marketapp.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group.marketapp.domain.Coupon;
import com.group.marketapp.domain.CouponType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponResponseDto {

    private Long id;
    private String name;
    private Integer quantity;
    private CouponType couponType;
    private LocalDateTime expiredDate;
    @JsonProperty("isActive")
    private boolean isActive;
    private int minimumMoney;
    private int discountPrice;

    public CouponResponseDto(Long id, String name, Integer quantity, CouponType couponType,LocalDateTime expiredDate, boolean isActive, int minimumMoney, int discountPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.couponType=couponType;
        this.expiredDate = expiredDate;
        this.isActive = isActive;
        this.minimumMoney = minimumMoney;
        this.discountPrice = discountPrice;
    }

    public static CouponResponseDto of(Coupon coupon) {
        return new CouponResponseDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getQuantity(),
                coupon.getCouponType(),
                coupon.getExpiredDate(),
                coupon.isActive(),
                coupon.getMinimumMoney(),
                coupon.getDiscountPrice()
        );
    }

}
