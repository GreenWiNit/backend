package com.example.green.domain.challengecert.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;

/**
 * 개인 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface PersonalChallengeParticipationRepository
	extends JpaRepository<PersonalChallengeParticipation, Long>, PersonalChallengeParticipationRepositoryCustom {

	/**
	 * 회원의 개인 챌린지 참여 여부를 확인합니다.
	 */
	boolean existsByMemberAndPersonalChallenge(Member member, PersonalChallenge challenge);

	/**
	 * 회원의 개인 챌린지 참여 정보를 조회합니다.
	 */
	Optional<PersonalChallengeParticipation> findByMemberAndPersonalChallenge(Member member,
		PersonalChallenge challenge);
}
