package com.group.marketapp.controller;

import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.dto.requestdto.CreateCouponRequestDto;
import com.group.marketapp.dto.requestdto.UpdateCouponRequestDto;
import com.group.marketapp.dto.responsedto.CouponResponseDto;
import com.group.marketapp.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "쿠폰 관리", description = "쿠폰 관련 API")
public class CouponController {

    private final CouponService couponService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "쿠폰 생성",
            description = "새로운 쿠폰을 생성합니다."
    )
    @PostMapping("/coupon")
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequestDto requestDto) {
        try {
            couponService.createCoupon(requestDto);
            return ResponseEntity.ok("Coupon created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create coupon: " + e.getMessage());
        }
    }

    @Operation(
            summary = "쿠폰 조회",
            description = "쿠폰 정보를 조회합니다."
    )
    @GetMapping("/coupon")
    public List<CouponResponseDto> getCoupons(){
        return couponService.getCoupon();
    }

    @Operation(
            summary = "쿠폰 업데이트",
            description = "쿠폰 정보를 조회합니다."
    )
    @PutMapping("/coupon")
    public ResponseEntity<?> updateCoupon(@RequestBody UpdateCouponRequestDto requestDto) {
        try {
            couponService.updateCoupon(requestDto);
            return ResponseEntity.ok("Coupon updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update coupon: " + e.getMessage());
        }
    }

    @Operation(
            summary = "선착순 쿠폰 발급",
            description = "선착순으로 쿠폰을 발급합니다."
    )
    @PostMapping("/coupon/{couponId}/issue")
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
