package com.group.marketapp.repository;

import com.group.marketapp.domain.ReceivedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedCouponRepository extends JpaRepository<ReceivedCoupon, Long> {
}
