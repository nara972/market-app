package com.group.marketapp.order.controller;


import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.service.OrderService;
import com.group.marketapp.user.domain.LoginUser;
import com.group.marketapp.user.domain.User;
import com.group.marketapp.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static com.group.marketapp.common.constant.SessionConstant.SESSION_ID;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/order/{loginId}")
    public void createOrder(@PathVariable String loginId, @RequestBody CreateOrderRequestDto request) {
        User loginUser = (User) redisTemplate.opsForValue().get("USER_SESSION_" + loginId);

        if (loginUser == null) {
            throw new IllegalArgumentException("로그인 후 주문 가능합니다.");
        }

        orderService.createOrder(request, loginUser.getId());
    }

}
