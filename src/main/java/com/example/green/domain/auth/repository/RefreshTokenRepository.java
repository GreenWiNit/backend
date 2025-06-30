package com.example.green.domain.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.auth.entity.RefreshToken;
import com.example.green.domain.member.entity.Member;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	// 토큰 값으로 조회 (유효한 토큰만)
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.tokenValue = :tokenValue AND rt.isRevoked = false")
	Optional<RefreshToken> findByTokenValueAndNotRevoked(@Param("tokenValue") String tokenValue);

	// 멤버의 모든 유효한 RefreshToken 조회
	@Query("SELECT rt FROM RefreshToken rt WHERE rt.member = :member AND rt.isRevoked = false")
	List<RefreshToken> findAllByMemberAndNotRevoked(@Param("member") Member member);

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

	// 사용자의 활성 세션 수 조회
	@Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.member = :member AND rt.isRevoked = false AND rt.expiresAt > :now")
	long countActiveSessionsByMember(@Param("member") Member member, @Param("now") LocalDateTime now);

	// 특정 IP 주소의 RefreshToken 조회
	List<RefreshToken> findByIpAddressAndIsRevokedFalse(String ipAddress);
} 