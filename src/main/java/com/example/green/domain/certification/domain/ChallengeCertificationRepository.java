package com.example.green.domain.certification.domain;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
