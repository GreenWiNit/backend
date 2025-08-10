package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;

public interface PersonalChallengeRepository extends JpaRepository<PersonalChallenge, Long> {

	@Query("SELECT COUNT(p) FROM PersonalChallenge p WHERE DATE(p.createdDate) = DATE(:date)")
	long countChallengesByCreatedDate(LocalDateTime date);
}
