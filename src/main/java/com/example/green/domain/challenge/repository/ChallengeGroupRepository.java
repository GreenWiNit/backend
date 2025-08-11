package com.example.green.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

	boolean existsByIdAndLeaderId(Long id, Long leaderId);

	@Query("""
			SELECT COUNT(p) > 0
			FROM ChallengeGroupParticipation p
			WHERE p.challengeGroup.id = :groupId
			AND p.memberId = :memberId
		""")
	boolean existMembership(Long groupId, Long memberId);
}
