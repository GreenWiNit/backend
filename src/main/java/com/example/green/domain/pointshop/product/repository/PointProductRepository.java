package com.example.green.domain.pointshop.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.Code;

public interface PointProductRepository extends JpaRepository<PointProduct, Long> {
	boolean existsByCode(Code code);

	boolean existsByCodeAndIdNot(Code code, Long id);
}
