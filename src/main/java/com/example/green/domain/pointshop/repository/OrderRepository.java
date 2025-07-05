package com.example.green.domain.pointshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
