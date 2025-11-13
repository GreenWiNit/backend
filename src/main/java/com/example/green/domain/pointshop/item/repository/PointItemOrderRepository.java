package com.example.green.domain.pointshop.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.item.entity.OrderPointItem;

public interface PointItemOrderRepository extends JpaRepository<OrderPointItem, Long>, PointItemOrderQueryRepository {
}
