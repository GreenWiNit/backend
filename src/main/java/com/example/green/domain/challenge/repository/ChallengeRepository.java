package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.exception.ChallengeException;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	default Challenge findByIdWithThrow(Long id) {
		return findById(id).orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	@Query("""
		SELECT p.challenge
		FROM Participation p
		WHERE p.challenge.id = :challengeId
		AND p.memberId = :memberId
		AND p.challenge.display = 'VISIBLE'
		""")
	Optional<Challenge> findChallengeByMembership(Long challengeId, Long memberId);
}
