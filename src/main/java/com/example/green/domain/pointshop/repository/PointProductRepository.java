package com.example.green.domain.pointshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;

public interface PointProductRepository extends JpaRepository<PointProduct, Long> {
	boolean existsByCode(Code code);

	boolean existsByCodeAndIdNot(Code code, Long id);
}
