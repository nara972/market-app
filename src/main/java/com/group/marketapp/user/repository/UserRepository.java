package com.group.marketapp.user.repository;

import com.group.marketapp.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginId(String loginId);
}
