package com.example.green.domain.challengecert.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;

/**
 * 개인 챌린지 인증 정보를 관리하는 레포지토리
 */
public interface PersonalChallengeCertificationRepository extends JpaRepository<PersonalChallengeCertification, Long> {

	/**
	 * 특정 참여와 인증 날짜로 인증 정보 조회
	 * (하루 한 번 인증 제약조건 확인용)
	 */
	Optional<PersonalChallengeCertification> findByParticipationAndCertifiedDate(
		PersonalChallengeParticipation participation, 
		LocalDate certifiedDate
	);

	/**
	 * 특정 참여와 인증 날짜에 인증 존재 여부 확인
	 * (하루 한 번 인증 제약조건 확인용)
	 */
	boolean existsByParticipationAndCertifiedDate(
		PersonalChallengeParticipation participation, 
		LocalDate certifiedDate
	);
} 