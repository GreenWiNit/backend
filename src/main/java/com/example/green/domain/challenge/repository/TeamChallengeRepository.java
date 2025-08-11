package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.challenge.TeamChallenge;

@Repository
public interface TeamChallengeRepository extends JpaRepository<TeamChallenge, Long> {

	@Query("""
		SELECT COUNT(tc) > 0
		FROM TeamChallenge tc
		WHERE tc.id = :challengeId
		AND tc.challengeStatus = 'PROCEEDING'
		AND tc.displayStatus = 'VISIBLE'
		AND tc.beginDateTime <= :groupBegin
		AND tc.endDateTime >= :groupEnd
		""")
	boolean isGroupPeriodValidForChallenge(Long challengeId, LocalDateTime groupBegin, LocalDateTime groupEnd);
}
