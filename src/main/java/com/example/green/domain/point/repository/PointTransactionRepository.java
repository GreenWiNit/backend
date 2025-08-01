package com.example.green.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;

import jakarta.persistence.LockModeType;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT pt.balanceAfter FROM PointTransaction pt WHERE pt.memberId = :memberId ORDER BY pt.id DESC LIMIT 1")
	Optional<PointAmount> findLatestBalance(@Param("memberId") Long memberId);
}
