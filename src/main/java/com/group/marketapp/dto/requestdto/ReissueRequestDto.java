package com.group.marketapp.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueRequestDto {

    @NotEmpty(message = "accessToken을 입력해주세요")
    private String accessToken;

    @NotEmpty(message = "refreshToken을 입력해주세요")
    private String refreshToken;

}
