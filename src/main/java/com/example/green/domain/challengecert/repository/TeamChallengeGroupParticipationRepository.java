package com.example.green.domain.challengecert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.group.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.group.TeamChallengeGroupParticipation;
import com.example.green.domain.challenge.entity.challenge.TeamChallengeParticipation;
import com.example.green.domain.challenge.entity.group.GroupRoleType;

public interface TeamChallengeGroupParticipationRepository
	extends JpaRepository<TeamChallengeGroupParticipation, Long> {

	/**
	 * 특정 팀 챌린지 참여에 대한 그룹 참여 정보 존재 여부 확인
	 */
	boolean existsByTeamChallengeParticipation(TeamChallengeParticipation teamChallengeParticipation);

	/**
	 * 특정 팀 챌린지 참여와 그룹에 대한 참여 정보 존재 여부 확인
	 */
	boolean existsByTeamChallengeParticipationAndTeamChallengeGroup(
		TeamChallengeParticipation teamChallengeParticipation,
		TeamChallengeGroup teamChallengeGroup
	);

	/**
	 * 특정 그룹과 사용자에 대한 특정 역할 참여 정보 존재 여부 확인
	 */
	boolean existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
		Long groupId,
		Long memberId,
		GroupRoleType groupRoleType
	);

	/**
	 * 특정 그룹과 사용자에 대한 참여 정보 존재 여부 확인
	 */
	boolean existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberId(
		Long groupId,
		Long memberId
	);

	/**
	 * 특정 그룹의 모든 참여자 목록 조회
	 */
	List<TeamChallengeGroupParticipation> findByTeamChallengeGroup(TeamChallengeGroup teamChallengeGroup);

	/**
	 * 특정 그룹의 모든 참여 정보 삭제
	 */
	void deleteByTeamChallengeGroup(TeamChallengeGroup teamChallengeGroup);
}
