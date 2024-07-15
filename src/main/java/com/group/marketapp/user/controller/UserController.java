package com.group.marketapp.user.controller;

import static com.group.marketapp.common.constant.SessionConstant.SESSION_ACTIVE;
import static com.group.marketapp.common.constant.SessionConstant.SESSION_ID;

import com.group.marketapp.user.domain.LoginUser;
import com.group.marketapp.user.domain.User;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody CreateUserRequestDto request){
        userService.createUser(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto request){
        boolean success = userService.loginUser(request);
        return success ? "Login successful"  : "Login failed";
    }

    @PostMapping("/logout")
    public String logout(@RequestBody LoginRequestDto request){
        userService.logoutUser(request.getLoginId());
        return "Logout successful";
    }

    @GetMapping("/session/{loginId}")
    public User getUserFromSession(@PathVariable String loginId){
        return userService.getUserFromSession(loginId);
    }
}
