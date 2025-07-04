package com.example.green.domain.pointshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;

public interface PointProductRepository extends JpaRepository<PointProduct, Long> {
	boolean existsByBasicInfoCode(String productCode);
}
