package com.example.green.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;

public interface PersonalChallengeRepository extends JpaRepository<PersonalChallenge, Long> {

	@Query("SELECT COUNT(p) FROM PersonalChallengeParticipation p WHERE p.personalChallenge.id = :personalChallengeId")
	long countParticipantByChallenge(Long personalChallengeId);

	@Query("""
		SELECT count(p) > 0 FROM PersonalChallengeParticipation p
		WHERE p.personalChallenge.id=:challengeId
		AND p.memberId=:memberId
		""")
	boolean existsMembership(Long challengeId, Long memberId);
}
