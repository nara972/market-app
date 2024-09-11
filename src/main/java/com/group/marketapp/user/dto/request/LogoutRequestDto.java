package com.group.marketapp.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDto {

    @NotEmpty(message = "잘못된 요청입니다.")
    private String accessToken;

    @NotEmpty(message = "잘못된 요청입니다.")
    private String refreshToken;

}
