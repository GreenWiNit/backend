package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.exception.ChallengeException;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	default Challenge findByIdWithThrow(Long id) {
		return findById(id).orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}
}
