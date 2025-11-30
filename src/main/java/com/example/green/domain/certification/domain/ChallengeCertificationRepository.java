package com.example.green.domain.certification.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.certification.infra.projections.MemberCertifiedCountProjection;

public interface ChallengeCertificationRepository extends JpaRepository<ChallengeCertification, Long> {

	@Query("""
		SELECT COUNT(cc) > 0
		FROM ChallengeCertification cc
		WHERE cc.challenge.challengeId = :challengeId
		AND cc.certifiedDate = :challengeDate
		AND cc.challenge.type = 'T'
		AND cc.member.memberId = :memberId
		""")
	boolean existsByTeamChallenge(Long challengeId, LocalDate challengeDate, Long memberId);

	@Query("""
		SELECT COUNT(cc) > 0
		FROM ChallengeCertification cc
		WHERE cc.challenge.challengeId = :challengeId
		AND cc.certifiedDate = :challengeDate
		AND cc.challenge.type = 'P'
		AND cc.member.memberId = :memberId
		""")
	boolean existsByPersonalChallenge(Long challengeId, LocalDate challengeDate, Long memberId);

	int countChallengeCertificationByMemberMemberId(Long memberId);

	//여러 회원 인증 수 (한번에 조회)
	@Query("SELECT c.member.memberId AS memberId, COUNT(c) AS certifiedCount " +
		"FROM ChallengeCertification c " +
		"WHERE c.member.memberId IN :memberIds " +
		"GROUP BY c.member.memberId")
	List<MemberCertifiedCountProjection> findCertifiedCountByMemberIds(@Param("memberIds") List<Long> memberIds);

}
