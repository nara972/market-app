package com.group.marketapp.coupon.service;

import com.group.marketapp.coupon.domain.Coupon;
import com.group.marketapp.coupon.dto.request.CreateCouponRequestDto;
import com.group.marketapp.coupon.dto.request.UpdateCouponRequestDto;
import com.group.marketapp.coupon.dto.response.CouponResponseDto;
import com.group.marketapp.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public void createCoupon(CreateCouponRequestDto request){

        Coupon coupon = Coupon.builder()
                        .name(request.getName())
                        .expiredDate(request.getExpiredDate())
                        .minimumMoney(request.getMinimumMoney())
                        .discountPrice(request.getDiscountPrice()).build();

        couponRepository.save(coupon);
    }

    public List<CouponResponseDto> getCoupon(){
        return couponRepository.findAll().stream()
                .map(CouponResponseDto::of)
                .collect(Collectors.toList());
    }

    public void updateCoupon(UpdateCouponRequestDto request){
        Coupon coupon=couponRepository.findById(request.getId()).orElseThrow(IllegalArgumentException::new);
        coupon.update(request);
        couponRepository.save(coupon);
    }

}
