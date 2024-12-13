package com.group.marketapp.controller;


import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.dto.requestdto.CreateOrderRequestDto;
import com.group.marketapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "주문 관리", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "주문 생성",
            description = "새로운 주문을 생성합니다."
    )
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String authorization,@RequestBody CreateOrderRequestDto request) {
        try {
            // Authorization 헤더 검증
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is required.");
            }

            String token = authorization.substring(7);  // Bearer 제거
            String loginId = jwtTokenProvider.extractLoginId(token);  // JWT에서 loginId 추출

            // Redis에서 세션 토큰 확인
            String sessionToken = (String) redisTemplate.opsForValue().get("RT:" + loginId);
            if (sessionToken == null || !loginId.equals(jwtTokenProvider.extractLoginId(sessionToken))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session data is invalid. Please log in again.");
            }

            // 주문 생성
            orderService.createOrder(request, loginId);
            return ResponseEntity.ok("Order created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order: " + e.getMessage());
        }
    }
    @Operation(
            summary = "주문 취소",
            description = "해당 주문을 취소합니다."
    )
    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok("Order cancelled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order ID: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel order: " + e.getMessage());
        }
    }

}
