package com.group.marketapp.user.controller;

import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.dto.request.LogoutRequestDto;
import com.group.marketapp.user.dto.response.UserResponseDto;
import com.group.marketapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public void createUser(@RequestBody CreateUserRequestDto request){
        userService.createUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request){
        try {
            UserResponseDto userInfo = userService.loginUser(request);
            return ResponseEntity.ok(userInfo);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    /**
    @PostMapping("/logout")
    public String logout(@RequestBody LoginRequestDto request){
        userService.logoutUser(request.getLoginId());
        return "Logout successful";
    }**/

    /**
    @PostMapping("/logout")
    public String logout(@RequestBody LogoutRequestDto request){
        userService.logoutUser(request);
        return "Logout successful";
    }**/

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization,@RequestBody LogoutRequestDto request) {

        String accessToken = authorization.substring(7);
        System.out.println("logout token: " + accessToken);

        try{
            userService.logoutUser(accessToken,request.getRefreshToken());
            return ResponseEntity.ok("Logout successful");
        }catch (Exception e){
            log.error("Logout failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }

    }


    /**
    @GetMapping("/session/{loginId}")
    public Users getUserFromSession(@PathVariable String loginId){
        return userService.getUserFromSession(loginId);
    }

    **/
}
