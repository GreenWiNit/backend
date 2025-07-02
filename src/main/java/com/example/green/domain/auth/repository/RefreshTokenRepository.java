package com.example.green.domain.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.auth.model.entity.RefreshToken;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.LockModeType;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	// 토큰 값으로 조회 (유효한 토큰만)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.tokenValue = :tokenValue AND rt.isRevoked = false")
	Optional<RefreshToken> findByTokenValueAndNotRevoked(@Param("tokenValue") String tokenValue);

	// 멤버의 모든 유효한 RefreshToken 조회 (생성 순서로 정렬) - 일반 조회
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member = :member AND rt.isRevoked = false ORDER BY rt.id ASC")
	List<RefreshToken> findAllByMemberAndNotRevoked(@Param("member") Member member);

	// 토큰 정리 전용: 비관적 락으로 복합 로직의 원자성 보장
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member = :member AND rt.isRevoked = false ORDER BY rt.id ASC")
	List<RefreshToken> findAllByMemberForCleanupWithLock(@Param("member") Member member);

	// 멤버의 모든 RefreshToken 무효화 (로그아웃 올 디바이스)
	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.member = :member AND rt.isRevoked = false")
	void revokeAllByMember(@Param("member") Member member);

	// 특정 멤버의 만료된 토큰 삭제
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.member = :member AND (rt.expiresAt < :now OR rt.isRevoked = true)")
	void deleteExpiredAndRevokedTokensByMember(@Param("member") Member member, @Param("now") LocalDateTime now);

	// 만료된 토큰 일괄 삭제 (스케줄러용)
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now OR rt.isRevoked = true")
	void deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);

	/*
	 * TODO: 향후 소프트 딜리트 + 배치 시스템 구현 시 추가할 메서드들
	 */
}
