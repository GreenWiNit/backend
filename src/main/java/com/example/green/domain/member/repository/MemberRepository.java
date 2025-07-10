package com.example.green.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);
	
	/**
	 * 활성 회원만 조회 (탈퇴하지 않은 회원)
	 * - MemberStatus가 NORMAL이고 deleted가 false인 회원
	 */
	@Query("SELECT m FROM Member m WHERE m.username = :username AND m.status = :status AND m.deleted = false")
	Optional<Member> findActiveByUsername(@Param("username") String username, @Param("status") MemberStatus status);
	
	/**
	 * 활성 회원만 조회 (오버로드 - NORMAL 상태 기본값)
	 */
	default Optional<Member> findActiveByUsername(String username) {
		return findActiveByUsername(username, MemberStatus.NORMAL);
	}
	
	/**
	 * 활성 회원 존재 여부 확인
	 */
	@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.username = :username AND m.status = :status AND m.deleted = false")
	boolean existsActiveByUsername(@Param("username") String username, @Param("status") MemberStatus status);
	
	/**
	 * 활성 회원 존재 여부 확인 (오버로드 - NORMAL 상태 기본값)
	 */
	default boolean existsActiveByUsername(String username) {
		return existsActiveByUsername(username, MemberStatus.NORMAL);
	}
}
