package com.group.marketapp.repository;

import com.group.marketapp.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserId(Long userId);

    void deleteById(Long id);

}
