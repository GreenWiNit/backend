package com.example.green.domain.pointshop.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
