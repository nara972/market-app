package com.group.marketapp.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCouponRequestDto {

    private String name;
    private Integer quantity;
    private LocalDateTime expiredDate;
    @JsonProperty("isActive")
    private boolean isActive;
    private int minimumMoney;
    private int discountPrice;

}
