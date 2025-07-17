package com.example.green.domain.challengecert.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;

public interface TeamChallengeGroupParticipationRepository
	extends JpaRepository<TeamChallengeGroupParticipation, Long> {

	/**
	 * 특정 팀 챌린지 참여에 대한 그룹 참여 정보 조회
	 */
	Optional<TeamChallengeGroupParticipation> findByTeamChallengeParticipation(
		TeamChallengeParticipation teamChallengeParticipation
	);

	/**
	 * 특정 그룹의 특정 역할을 가진 참여 정보 조회
	 */
	Optional<TeamChallengeGroupParticipation> findByTeamChallengeGroupAndGroupRoleType(
		TeamChallengeGroup teamChallengeGroup,
		GroupRoleType groupRoleType
	);

	/**
	 * 특정 그룹의 참여자 수 조회
	 */
	long countByTeamChallengeGroup(TeamChallengeGroup teamChallengeGroup);

	/**
	 * 특정 팀 챌린지 참여에 대한 그룹 참여 정보 존재 여부 확인
	 */
	boolean existsByTeamChallengeParticipation(TeamChallengeParticipation teamChallengeParticipation);

	/**
	 * 특정 그룹에 특정 역할을 가진 참여자가 존재하는지 확인
	 */
	boolean existsByTeamChallengeGroupAndGroupRoleType(
		TeamChallengeGroup teamChallengeGroup,
		GroupRoleType groupRoleType
	);
}
