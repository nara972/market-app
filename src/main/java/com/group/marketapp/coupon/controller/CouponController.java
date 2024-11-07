package com.group.marketapp.coupon.controller;

import com.group.marketapp.coupon.dto.request.CreateCouponRequestDto;
import com.group.marketapp.coupon.dto.request.UpdateCouponRequestDto;
import com.group.marketapp.coupon.dto.response.CouponResponseDto;
import com.group.marketapp.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/coupon")
    public void createCoupon(@RequestBody CreateCouponRequestDto requestDto){
        couponService.createCoupon(requestDto);
    }

    @GetMapping("/coupon")
    public List<CouponResponseDto> getCoupons(){
        return couponService.getCoupon();
    }

    @PutMapping("/coupon")
    public void updateCoupon(@RequestBody UpdateCouponRequestDto requestDto){
        couponService.updateCoupon(requestDto);
    }

}
