package com.group.marketapp.user.controller;

import static com.group.marketapp.common.constant.SessionConstant.SESSION_ACTIVE;
import static com.group.marketapp.common.constant.SessionConstant.SESSION_ID;

import com.group.marketapp.user.domain.LoginUser;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody CreateUserRequestDto request){
        userService.createUser(request);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto request, HttpServletRequest httprequest){
        LoginUser loginUser = userService.loginUser(request);
        httprequest.getSession(SESSION_ACTIVE).setAttribute(SESSION_ID,loginUser);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest httprequest){
        httprequest.getSession(SESSION_ACTIVE).invalidate();
    }


}
