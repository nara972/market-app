package com.group.marketapp.user.service;

import com.group.marketapp.user.domain.LoginUser;
import com.group.marketapp.user.domain.User;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.dto.request.LoginRequestDto;
import com.group.marketapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

    public LoginUser loginUser(LoginRequestDto request){

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(IllegalArgumentException::new);

        if(user.isPasswordEqual(request.getPassword())){
            return new LoginUser(user.getId(),user.getUsername());
        }
        else{
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

    }


}
