package com.group.marketapp.repository;

import com.group.marketapp.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("select c from Coupon c where c.isActive=true")
    List<Coupon> findAll();

    @Query("select c from Coupon c where c.expiredDate < CURRENT_TIMESTAMP and c.isActive = true")
    List<Coupon> findExpiredCoupons();

}
