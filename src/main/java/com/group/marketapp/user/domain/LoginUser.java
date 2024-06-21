package com.group.marketapp.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginUser {

    private Long id;
    private String username;

    public LoginUser(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
