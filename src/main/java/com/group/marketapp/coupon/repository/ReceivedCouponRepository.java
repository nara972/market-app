package com.group.marketapp.coupon.repository;

import com.group.marketapp.coupon.domain.ReceivedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedCouponRepository extends JpaRepository<ReceivedCoupon, Long> {
}
