package com.group.marketapp.coupon.controller;

import com.group.marketapp.common.Authentication;
import com.group.marketapp.common.LoginContext;
import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.coupon.dto.request.CreateCouponRequestDto;
import com.group.marketapp.coupon.dto.request.UpdateCouponRequestDto;
import com.group.marketapp.coupon.dto.response.CouponResponseDto;
import com.group.marketapp.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

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

    //선착순 쿠폰 발급 API
    @PostMapping("/coupon/{couponId}/issue")
    @Authentication
    public CouponResponseDto issueCoupon(@PathVariable Long couponId,@RequestHeader("Authorization") String authorization){

        //Authorization 헤더 검증
        if(authorization == null || !authorization.startsWith("Bearer ")){
            throw new IllegalArgumentException("Authorization header is incorrect");
        }
        String token = authorization.substring(7);
        String loginId=jwtTokenProvider.extractLoginId(token);

        //Redis에서 세션 토큰 확인
        String sessionToken=(String)redisTemplate.opsForValue().get("RT:"+loginId);
        if(sessionToken==null || !loginId.equals(jwtTokenProvider.extractLoginId(sessionToken))){
            throw new IllegalArgumentException("Session data is invalid. Please log in again.");
        }
        return couponService.issueCoupon(couponId, loginId);
    }

}
