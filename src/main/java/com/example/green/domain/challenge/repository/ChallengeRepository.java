package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	default Challenge findByIdWithThrow(Long id) {
		return findById(id).orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	@Query("SELECT COUNT(p) FROM Participation p "
		+ "WHERE p.challenge.id = :personalChallengeId "
		+ "AND p.challenge.type = :type")
	long countParticipantByChallenge(Long challengeId, ChallengeType type);

	@Query("""
		SELECT count(p) > 0 FROM Participation p
		WHERE p.challenge.id = :challengeId
		AND p.memberId =:memberId
		""")
	boolean existsMembership(Long challengeId, Long memberId);
}
