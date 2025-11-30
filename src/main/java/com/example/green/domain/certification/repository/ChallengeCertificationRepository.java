package com.example.green.domain.certification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.infra.projections.MemberCertifiedCountProjection;

public interface ChallengeCertificationRepository extends JpaRepository<ChallengeCertification, Long> {

	//단일 회원 인증 수
	int countChallengeCertificationByMemberMemberId(Long memberId);

	//여러 회원 인증 수 (한번에 조회)
	@Query("SELECT c.member.memberId AS memberId, COUNT(c) AS certifiedCount " +
		"FROM ChallengeCertification c " +
		"WHERE c.member.memberId IN :memberIds " +
		"GROUP BY c.member.memberId")
	List<MemberCertifiedCountProjection> findCertifiedCountByMemberIds(@Param("memberIds") List<Long> memberIds);
}
