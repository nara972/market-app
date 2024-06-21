package com.group.marketapp.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @NotBlank(message = "아이디를 입력하지 않았습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    private String password;

}
