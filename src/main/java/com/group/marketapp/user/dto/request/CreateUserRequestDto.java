package com.group.marketapp.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class CreateUserRequestDto {

    @NotBlank(message = "아이디를 입력하지 않았습니다 .")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    private String password;

    @NotBlank(message = "이름을 입력하지 않았습니다.")
    private String username;

    @NotBlank(message = "주소를 입력하지 않았습니다.")
    private String address;

}
