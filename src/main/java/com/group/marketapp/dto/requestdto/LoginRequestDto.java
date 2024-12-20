package com.group.marketapp.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class LoginRequestDto {

    @NotBlank(message = "아이디를 입력하지 않았습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하지 않았습니다.")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(loginId, password);
    }
}
