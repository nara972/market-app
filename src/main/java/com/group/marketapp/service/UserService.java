package com.group.marketapp.service;

import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.domain.Users;
import com.group.marketapp.dto.requestdto.CreateUserRequestDto;
import com.group.marketapp.dto.requestdto.LoginRequestDto;
import com.group.marketapp.dto.responsedto.UserResponseDto;
import com.group.marketapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void checkRedisTemplate(){
        if(redisTemplate == null){
            throw new IllegalStateException("RedisTemplate has not been initialized");
        }
    }

    public void createUser(CreateUserRequestDto request){

        if(userRepository.findByLoginId(request.getLoginId()).isPresent()){
            throw new IllegalArgumentException("The ID already exists.");
        }

        Users user = Users.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .address(request.getAddress())
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public UserResponseDto loginUser(LoginRequestDto request){

        Users user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()->new IllegalArgumentException("The ID does not exist."));

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto tokenInfo = jwtTokenProvider.generateToken(authentication);

        tokenInfo.setUsername(user.getUsername());
        tokenInfo.setRole(user.getRole());

        log.info("tokenInfo : {}", tokenInfo);

        redisTemplate.opsForValue().set("RT:"+authentication.getName(),tokenInfo.getRefreshToken()
                ,tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;

    }

    public void logoutUser(String accessToken,String refreshToken){

        if(!jwtTokenProvider.validateToken(accessToken)){
            log.error("Invalid access token during logout.");
            throw new RuntimeException("Invalid access token.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        if(redisTemplate.opsForValue().get("RT:"+authentication.getName())!=null){
            redisTemplate.delete("RT:"+authentication.getName());
        }

        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.SECONDS);
    }

    public UserResponseDto refreshAccessToken(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("Invalid Refresh Token.");
            throw new RuntimeException("Invalid Refresh Token.");
        }

        String userId = jwtTokenProvider.extractLoginId(refreshToken);
        log.info("Extracted userId from Refresh Token: {}", userId);

        String storedRefreshToken = (String) redisTemplate.opsForValue().get("RT:" + userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new IllegalArgumentException("Stored Refresh Token does not match.");
        }

        Authentication authentication = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken);
        UserResponseDto newToken = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                "RT:" + userId,
                newToken.getRefreshToken(),
                newToken.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        log.info("New tokens generated for userId: {}", userId);

        return newToken;
    }

}