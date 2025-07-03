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

	// username으로 모든 유효한 RefreshToken 조회 (Auth 서비스용)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member.username = :username AND rt.isRevoked = false ORDER BY rt.id ASC")
	List<RefreshToken> findAllByUsernameAndNotRevoked(@Param("username") String username);

	// username으로 가장 최신 RefreshToken 조회 (로그아웃용)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member.username = :username AND rt.isRevoked = false ORDER BY rt.tokenVersion DESC, rt.id DESC")
	Optional<RefreshToken> findLatestByUsernameAndNotRevoked(@Param("username") String username);

	// 토큰 정리 전용: 비관적 락으로 복합 로직의 원자성 보장
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member = :member AND rt.isRevoked = false ORDER BY rt.id ASC")
	List<RefreshToken> findAllByMemberForCleanupWithLock(@Param("member") Member member);

	// 사용자의 모든 RefreshToken 무효화 (username 기반)
	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.member.username = :username AND rt.isRevoked = false")
	void revokeAllByUsername(@Param("username") String username);

	// 특정 사용자의 만료된 토큰 삭제 (username 기반)
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.member.username = :username AND (rt.expiresAt < :now OR rt.isRevoked = true)")
	void deleteExpiredAndRevokedTokensByUsername(@Param("username") String username, @Param("now") LocalDateTime now);

	// 만료된 토큰 일괄 삭제 (스케줄러용)
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now OR rt.isRevoked = true")
	void deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);

	/*
	 * TODO: 향후 소프트 딜리트 + 배치 시스템 구현 시 추가할 메서드들
	 */
}
