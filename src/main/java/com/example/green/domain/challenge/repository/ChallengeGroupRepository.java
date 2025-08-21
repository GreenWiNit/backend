package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

	@Query("""
		SELECT CASE WHEN count(p.id) > 0 THEN TRUE ELSE FALSE END
		FROM ChallengeGroupParticipation p
		WHERE p.challengeGroup.id = :groupId
		AND p.memberId = :memberId
		""")
	boolean existMembership(Long groupId, Long memberId);

	@Query("""
		SELECT COUNT(p) > 0
		FROM ChallengeGroupParticipation p
		JOIN p.challengeGroup g
		WHERE p.memberId = :memberId
		AND g.period.beginDateTime >= :startOfDay
		AND g.period.beginDateTime < :endOfDay
		AND g.teamChallengeId = :teamChallengeId
		""")
	boolean existsParticipationOnActivityDate(
		Long memberId, Long teamChallengeId, LocalDateTime startOfDay, LocalDateTime endOfDay);

	Optional<ChallengeGroup> findByTeamCode(String teamCode);
}
