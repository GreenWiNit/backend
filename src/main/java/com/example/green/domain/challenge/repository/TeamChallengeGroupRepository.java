package com.example.green.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.TeamChallengeGroup;

@Repository
public interface TeamChallengeGroupRepository
	extends JpaRepository<TeamChallengeGroup, Long>, TeamChallengeGroupRepositoryCustom {

	/**
	 * 특정 팀 챌린지와 그룹 ID로 그룹을 조회합니다.
	 */
	@Query("SELECT tcg FROM TeamChallengeGroup tcg "
		+ "WHERE tcg.id = :groupId AND tcg.teamChallenge.id = :challengeId")
	Optional<TeamChallengeGroup> findByIdAndTeamChallengeId(@Param("groupId") Long groupId,
		@Param("challengeId") Long challengeId);

	/**
	 * 특정 팀 챌린지의 그룹 수를 조회합니다.
	 */
	@Query("SELECT COUNT(tcg) FROM TeamChallengeGroup tcg WHERE tcg.teamChallenge.id = :challengeId")
	long countByTeamChallengeId(@Param("challengeId") Long challengeId);
}
