package com.group.marketapp.order.controller;


import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.service.OrderService;
import com.group.marketapp.user.domain.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    /**
    @PostMapping("/order/{loginId}")
    public void createOrder(@PathVariable String loginId, @RequestBody CreateOrderRequestDto request) {
        Users loginUser = (Users) redisTemplate.opsForValue().get("USER_SESSION_" + loginId);

        if (loginUser == null) {
            throw new IllegalArgumentException("로그인 후 주문 가능합니다.");
        }

        orderService.createOrder(request, loginUser.getId());
    }**/

    @PostMapping("/order")
    public void createOrder(@RequestHeader("Authorization") String authorization, @RequestBody CreateOrderRequestDto request) {

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is required.");
        }

        String token = authorization.substring(7);  // Bearer 제거
        String loginId = jwtTokenProvider.extractLoginId(token);  // JWT에서 loginId 추출

        // Redis에서 세션 토큰 확인
        String sessionToken = (String) redisTemplate.opsForValue().get("RT:" + loginId);
        if (sessionToken == null || !loginId.equals(jwtTokenProvider.extractLoginId(sessionToken))) {
            throw new IllegalArgumentException("Session data is invalid. Please log in again.");
        }

        // 주문 생성
        orderService.createOrder(request, loginId);

    }

    @PostMapping("/order/{orderId}/cancel")
    public void cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
    }



}
