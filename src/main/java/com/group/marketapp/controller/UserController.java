package com.group.marketapp.controller;

import com.group.marketapp.dto.requestdto.CreateUserRequestDto;
import com.group.marketapp.dto.requestdto.LoginRequestDto;
import com.group.marketapp.dto.requestdto.LogoutRequestDto;
import com.group.marketapp.dto.responsedto.UserResponseDto;
import com.group.marketapp.service.UserService;
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
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDto request) {

        try {
            userService.createUser(request);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            UserResponseDto userInfo = userService.loginUser(request);
            return ResponseEntity.ok(userInfo);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization, @RequestBody LogoutRequestDto request) {

        String accessToken = authorization.substring(7);
        System.out.println("logout token: " + accessToken);

        try {
            userService.logoutUser(accessToken, request.getRefreshToken());
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            log.error("Logout failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }

    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String token = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;

            // Refresh Token을 이용한 새로운 Access Token 발급
            UserResponseDto newTokens = userService.refreshAccessToken(token);

            return ResponseEntity.ok(newTokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token renewal failed " + e.getMessage());
        }
    }
}
