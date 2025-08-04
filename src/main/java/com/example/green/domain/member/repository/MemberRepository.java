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
	/**
	 * 회원 키로 회원 조회
	 */
	@Query("SELECT m FROM Member m WHERE m.memberKey = :memberKey")
	Optional<Member> findByMemberKey(String memberKey);

	boolean existsByMemberKey(String memberKey);


	/**
	 * 활성 회원만 조회 (탈퇴하지 않은 회원)
	 * - MemberStatus가 NORMAL이고 deleted가 false인 회원
	 */
	@Query("SELECT m FROM Member m WHERE m.memberKey = :memberKey AND m.status = :status AND m.deleted = false")
	Optional<Member> findActiveByMemberKey(@Param("memberKey") String memberKey, @Param("status") MemberStatus status);

	/**
	 * 활성 회원만 조회 (오버로드 - NORMAL 상태 기본값)
	 */
	default Optional<Member> findActiveByMemberKey(String memberKey) {
		return findActiveByMemberKey(memberKey, MemberStatus.NORMAL);
	}

	/**
	 * 활성 회원 존재 여부 확인
	 */
	@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.memberKey = :memberKey AND m.status = :status AND m.deleted = false")
	boolean existsActiveByMemberKey(@Param("memberKey") String memberKey, @Param("status") MemberStatus status);

	/**
	 * 활성 회원 존재 여부 확인 (오버로드 - NORMAL 상태 기본값)
	 */
	default boolean existsActiveByMemberKey(String memberKey) {
		return existsActiveByMemberKey(memberKey, MemberStatus.NORMAL);
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


	@Query(value = "SELECT COUNT(*) FROM MEMBER WHERE BINARY NICKNAME = :nickname AND STATUS = 'NORMAL' AND DELETED = false", 
		   nativeQuery = true)
	Long countByNickname(@Param("nickname") String nickname);
}
