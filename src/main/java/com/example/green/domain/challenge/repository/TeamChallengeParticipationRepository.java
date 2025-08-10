package com.example.green.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallengeParticipation;

/**
 * 팀 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface TeamChallengeParticipationRepository extends JpaRepository<TeamChallengeParticipation, Long> {

	Optional<TeamChallengeParticipation> findByMemberIdAndTeamChallenge(Long memberId, TeamChallenge challenge);

	/**
	 * 팀 챌린지와 회원으로 참여 정보를 조회합니다.
	 */
	Optional<TeamChallengeParticipation> findByTeamChallengeAndMemberId(TeamChallenge teamChallenge, Long memberId);
}

