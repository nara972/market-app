package com.group.marketapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
public class Users implements UserDetails {

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

    private String role;



    @Builder
    public Users(Long id, String loginId, String password, String username, String address,String role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.address = address;
        this.role = role;
    }

    public Users() {

    }

    public boolean isPasswordEqual(String password) {
        return this.password.equals(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
