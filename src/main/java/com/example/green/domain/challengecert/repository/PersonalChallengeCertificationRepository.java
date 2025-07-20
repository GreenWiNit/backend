package com.example.green.domain.challengecert.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;

public interface PersonalChallengeCertificationRepository
	extends JpaRepository<PersonalChallengeCertification, Long>, PersonalChallengeCertificationRepositoryCustom {

	boolean existsByParticipationAndCertifiedDate(PersonalChallengeParticipation participation,
		LocalDate certifiedDate);
}
