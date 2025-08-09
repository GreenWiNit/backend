package com.example.green.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.member.entity.Member;

/**
 * 팀 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface TeamChallengeParticipationRepository
	extends JpaRepository<TeamChallengeParticipation, Long>, TeamChallengeParticipationRepositoryCustom {

	/**
	 * 회원의 팀 챌린지 참여 여부를 확인합니다.
	 */
	boolean existsByMemberAndTeamChallenge(Member member, TeamChallenge challenge);

	/**
	 * 회원의 팀 챌린지 참여 정보를 조회합니다.
	 */
	Optional<TeamChallengeParticipation> findByMemberAndTeamChallenge(Member member, TeamChallenge challenge);

	/**
	 * 팀 챌린지와 회원으로 참여 정보를 조회합니다.
	 */
	Optional<TeamChallengeParticipation> findByTeamChallengeAndMember(TeamChallenge teamChallenge, Member member);
}

