package com.group.marketapp.user.service;

import com.group.marketapp.user.domain.LoginUser;
import com.group.marketapp.user.domain.User;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .username(request.getUsername())
                .address(request.getAddress())
                .build();

        userRepository.save(user);
    }

    public boolean loginUser(LoginRequestDto request){

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if(user.isPasswordEqual(request.getPassword())){
           redisTemplate.opsForValue().set("USER_SESSION_" + user.getLoginId(), user);
           return true;
        }
        return false;
    }

    public void logoutUser(String loginId){
        redisTemplate.delete("USER_SESSION_" + loginId);
    }

    public User getUserFromSession(String loginId){
        return (User) redisTemplate.opsForValue().get("USER_SESSION_" + loginId);
    }

}
