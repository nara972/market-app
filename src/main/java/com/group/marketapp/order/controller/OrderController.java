package com.group.marketapp.order.controller;


import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.service.OrderService;
import com.group.marketapp.user.domain.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static com.group.marketapp.common.constant.SessionConstant.SESSION_ID;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public void CreateOrder(@RequestBody CreateOrderRequestDto request, HttpServletRequest httprequest){

        LoginUser loginUser = (LoginUser) httprequest.getSession().getAttribute(SESSION_ID);

        if(loginUser == null) {
            throw new IllegalArgumentException("로그인 후 주문 가능합니다.");
        }

        orderService.createOrder(request, loginUser.getId());

    }
}
