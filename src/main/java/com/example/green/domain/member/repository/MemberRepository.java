package com.example.green.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);
	
	Optional<Member> findByEmail(String email);
	
	boolean existsByEmail(String email);
	
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
	
	/**
	 * 관리자용 활성 회원 목록 조회 (페이징)
	 * - 탈퇴하지 않은 회원만 조회
	 * - 가입일순 내림차순 정렬
	 */
	@Query("SELECT m FROM Member m WHERE m.status = 'NORMAL' AND m.deleted = false")
	Page<Member> findActiveMembersForAdmin(Pageable pageable);
	
	/**
	 * 관리자용 활성 회원 전체 목록 조회 (엑셀 다운로드용)
	 * - 탈퇴하지 않은 회원만 조회
	 * - 가입일순 내림차순 정렬
	 */
	@Query("SELECT m FROM Member m WHERE m.status = 'NORMAL' AND m.deleted = false ORDER BY m.createdDate DESC")
	List<Member> findAllActiveMembersForAdmin();
	
	/**
	 * 관리자용 탈퇴 회원 목록 조회 (페이징)
	 * - 탈퇴한 회원만 조회
	 * - 탈퇴일순 내림차순 정렬
	 */
	@Query("SELECT m FROM Member m WHERE m.status = 'DELETED' OR m.deleted = true ORDER BY m.modifiedDate DESC")
	Page<Member> findWithdrawnMembersForAdmin(Pageable pageable);
	
	/**
	 * 관리자용 탈퇴 회원 전체 목록 조회 (엑셀 다운로드용)
	 * - 탈퇴한 회원만 조회
	 * - 탈퇴일순 내림차순 정렬
	 */
	@Query("SELECT m FROM Member m WHERE m.status = 'DELETED' OR m.deleted = true ORDER BY m.modifiedDate DESC")
	List<Member> findAllWithdrawnMembersForAdmin();
}
