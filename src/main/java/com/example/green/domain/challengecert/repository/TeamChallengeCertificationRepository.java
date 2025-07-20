package com.example.green.domain.challengecert.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;

public interface TeamChallengeCertificationRepository
	extends JpaRepository<TeamChallengeCertification, Long>, TeamChallengeCertificationRepositoryCustom {

	boolean existsByParticipationAndCertifiedDate(TeamChallengeParticipation participation, LocalDate certifiedDate);
}
