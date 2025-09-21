package com.example.green.domain.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.example.green.domain.point.entity.PointTransaction;

import jakarta.persistence.LockModeType;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PointTransaction> findFirstByMemberIdOrderByIdDesc(Long memberId);
}
