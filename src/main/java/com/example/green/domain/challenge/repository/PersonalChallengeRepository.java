package com.example.green.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.PersonalChallenge;

@Repository
public interface PersonalChallengeRepository
	extends JpaRepository<PersonalChallenge, Long>, PersonalChallengeRepositoryCustom {
}
