package com.group.marketapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ReceivedCoupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="coupon_id", nullable=false)
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private Users users;

    private LocalDateTime receivedDate;

    public ReceivedCoupon(Coupon coupon, Users users) {
        this.coupon=coupon;
        this.users = users;
        this.receivedDate = LocalDateTime.now();
    }
}
