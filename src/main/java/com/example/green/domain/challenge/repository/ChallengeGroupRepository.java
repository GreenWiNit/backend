package com.example.green.domain.challenge.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

	boolean existsByIdAndLeaderId(Long id, Long leaderId);

	@Query("""
		SELECT CASE WHEN EXISTS (
			SELECT 1
			FROM ChallengeGroupParticipation p
			WHERE p.challengeGroup.id = :groupId
			AND p.memberId = :memberId
		) THEN true ELSE false END
		""")
	boolean existMembership(Long groupId, Long memberId);

	@Query("""
		SELECT CASE WHEN EXISTS (
			SELECT 1
			FROM ChallengeGroup g
			JOIN ChallengeGroupParticipation p ON g.id = p.challengeGroup.id
			WHERE p.memberId = :memberId
			AND DATE(g.period.beginDateTime) = :activityDate
		) THEN true ELSE false END
		""")
	boolean existsParticipationOnActivityDate(Long memberId, LocalDate activityDate);
}
