package com.webdemo.backend.Reposity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdemo.backend.model.Order;
import com.webdemo.backend.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId); // Lấy đơn hàng theo user
    List<Order> findByStatus(OrderStatus status);
}