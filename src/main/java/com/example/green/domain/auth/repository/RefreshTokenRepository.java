package com.example.green.domain.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.auth.entity.TokenManager;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.LockModeType;

public interface RefreshTokenRepository extends JpaRepository<TokenManager, Long> {

	// 토큰 값으로 조회 (유효한 토큰만)
	@Query("SELECT rt FROM TokenManager rt WHERE rt.tokenValue = :tokenValue AND rt.isRevoked = false")
	Optional<TokenManager> findByTokenValueAndNotRevoked(@Param("tokenValue") String tokenValue);

	// memberKey로 모든 유효한 TokenManager 조회 (Auth 서비스용)
	@Query("SELECT rt FROM TokenManager rt " + "WHERE rt.member.memberKey = :memberKey AND rt.isRevoked = false "
		+ "ORDER BY rt.id ASC")
	List<TokenManager> findAllByMemberKeyAndNotRevoked(@Param("memberKey") String memberKey);

	// memberKey로 가장 최신 TokenManager 조회 (로그아웃용)
	@Query("SELECT rt FROM TokenManager rt "
		+ "WHERE rt.member.memberKey = :memberKey AND rt.isRevoked = false "
		+ "ORDER BY rt.tokenVersion DESC, rt.id DESC LIMIT 1")
	Optional<TokenManager> findLatestByMemberKeyAndNotRevoked(@Param("memberKey") String memberKey);

	// 토큰 정리 전용
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT rt FROM TokenManager rt WHERE rt.member = :member AND rt.isRevoked = false ORDER BY rt.id ASC")
	List<TokenManager> findAllByMemberForCleanupWithLock(@Param("member") Member member);

	// 사용자의 모든 TokenManager 무효화 (memberKey 기반)
	@Modifying
	@Query(
		"UPDATE TokenManager rt " + "SET rt.isRevoked = true "
			+ "WHERE rt.member.memberKey = :memberKey AND rt.isRevoked = false"
	)
	void revokeAllByMemberKey(@Param("memberKey") String memberKey);

	// 특정 사용자의 만료된 토큰 삭제 (memberKey 기반)
	@Modifying
	@Query("DELETE FROM TokenManager rt " + "WHERE rt.member.memberKey = :memberKey "
		+ "AND (rt.expiresAt < :now OR rt.isRevoked = true)")
	void deleteExpiredAndRevokedTokensByMemberKey(@Param("memberKey") String memberKey, @Param("now") LocalDateTime now);

	// 만료된 토큰 일괄 삭제 (스케줄러용)
	@Modifying
	@Query("DELETE FROM TokenManager rt WHERE rt.expiresAt < :now OR rt.isRevoked = true")
	void deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);
	/*
	 * TODO: 향후 소프트 딜리트 + 배치 시스템 구현 시 추가할 메서드들
	 */
}
