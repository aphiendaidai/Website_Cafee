package com.webdemo.backend.Reposity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webdemo.backend.model.OrderItem;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByUserIdOrderByIdDesc(Long userId);
}