package com.group.marketapp.user.service;

import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.dto.request.LogoutRequestDto;
import com.group.marketapp.user.dto.request.ReissueRequestDto;
import com.group.marketapp.user.dto.response.UserResponseDto;
import com.group.marketapp.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Users user = Users.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .address(request.getAddress())
                .build();

        userRepository.save(user);
    }

    /**
     public boolean loginUser(LoginRequestDto request){

     Users user = userRepository.findByLoginId(request.getLoginId())
     .orElseThrow(()->new IllegalArgumentException("존재하지 않는 아이디입니다."));

     if(user.isPasswordEqual(request.getPassword())){
     redisTemplate.opsForValue().set("USER_SESSION_" + user.getLoginId(), user);
     return true;
     }
     return false;
     }**/

    public UserResponseDto loginUser(LoginRequestDto request){

        Users user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 아이디입니다."));

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set("RT:"+authentication.getName(),tokenInfo.getRefreshToken()
                ,tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        //log.info("로그인시: {}", redisTemplate.opsForValue().get("RT:"+authentication.getName()));

        return tokenInfo;

    }


    /**
     public void logoutUser(String loginId){
     redisTemplate.delete("USER_SESSION_" + loginId);
     }**/

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

    public Users getUserFromSession(String loginId){
        return (Users) redisTemplate.opsForValue().get("USER_SESSION_" + loginId);
    }

}