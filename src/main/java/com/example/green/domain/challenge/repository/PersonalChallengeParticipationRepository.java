package com.example.green.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.PersonalChallengeParticipation;

/**
 * 개인 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface PersonalChallengeParticipationRepository
	extends JpaRepository<PersonalChallengeParticipation, Long>, PersonalChallengeParticipationRepositoryCustom {

	/**
	 * 회원의 개인 챌린지 참여 정보를 조회합니다.
	 */
	Optional<PersonalChallengeParticipation> findByMemberIdAndPersonalChallenge(Long memberId,
		PersonalChallenge challenge);
}
