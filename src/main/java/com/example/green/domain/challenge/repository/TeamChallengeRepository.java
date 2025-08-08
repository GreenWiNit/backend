package com.example.green.domain.challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.TeamChallenge;

@Repository
public interface TeamChallengeRepository extends JpaRepository<TeamChallenge, Long>, TeamChallengeRepositoryCustom {
	
	Optional<TeamChallenge> findByChallengeCode(String challengeCode);
}
