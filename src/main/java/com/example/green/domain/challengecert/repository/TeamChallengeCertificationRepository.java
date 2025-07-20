package com.example.green.domain.challengecert.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;

/**
 * 팀 챌린지 인증 정보를 관리하는 레포지토리
 */
public interface TeamChallengeCertificationRepository extends JpaRepository<TeamChallengeCertification, Long> {

	/**
	 * 특정 참여와 인증 날짜로 인증 정보 조회
	 * (하루 한 번 인증 제약조건 확인용)
	 */
	Optional<TeamChallengeCertification> findByParticipationAndCertifiedDate(
		TeamChallengeParticipation participation, 
		LocalDate certifiedDate
	);

	/**
	 * 특정 참여와 인증 날짜에 인증 존재 여부 확인
	 * (하루 한 번 인증 제약조건 확인용)
	 */
	boolean existsByParticipationAndCertifiedDate(
		TeamChallengeParticipation participation, 
		LocalDate certifiedDate
	);
} 