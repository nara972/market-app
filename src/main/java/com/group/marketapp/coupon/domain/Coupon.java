package com.group.marketapp.coupon.domain;

import com.group.marketapp.coupon.dto.request.UpdateCouponRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String name;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive=true;

    @Column(name = "minimun_money", nullable = false)
    private int minimumMoney;

    @Column(name = "discount_price", nullable = false)
    private int discountPrice;

    public Coupon() {

    }

    @Builder
    public Coupon(String name, LocalDateTime expiredDate, boolean isActive, Integer minimumMoney, int discountPrice) {
        this.name = name;
        this.expiredDate = expiredDate;
        this.minimumMoney = minimumMoney;
        this.discountPrice = discountPrice;
    }

    public void update(UpdateCouponRequestDto dto){
        this.name=dto.getName();
        this.expiredDate=dto.getExpiredDate();
        this.isActive=dto.isActive();
        this.minimumMoney=dto.getMinimumMoney();
        this.discountPrice=dto.getDiscountPrice();
    }

    public void checkAndDeactivateIfExpired() {
        if(expiredDate != null && expiredDate.isBefore(LocalDateTime.now())) {
            this.isActive=false;
        }
    }

}
