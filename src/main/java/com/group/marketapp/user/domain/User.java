package com.group.marketapp.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true, nullable = false)
    private String loginId;

    @Column(length = 256, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String address;

    @Builder
    public User(Long id,String loginId, String password, String username, String address) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public User() {

    }

    public boolean isPasswordEqual(String password) {
        return this.password.equals(password);
    }

}
