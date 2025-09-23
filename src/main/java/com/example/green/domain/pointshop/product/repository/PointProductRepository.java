package com.example.green.domain.pointshop.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.Code;

import jakarta.persistence.LockModeType;

public interface PointProductRepository extends JpaRepository<PointProduct, Long> {
	boolean existsByCode(Code code);

	boolean existsByCodeAndIdNot(Code code, Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from PointProduct p where p.id = :id")
	Optional<PointProduct> findByIdWithPessimisticLock(Long id);
}
