package com.group.marketapp.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserResponseDto {


        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;


}
