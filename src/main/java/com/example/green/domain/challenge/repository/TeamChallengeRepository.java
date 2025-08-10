package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.TeamChallenge;

@Repository
public interface TeamChallengeRepository extends JpaRepository<TeamChallenge, Long> {

	@Query("SELECT COUNT(t) FROM TeamChallenge t WHERE DATE(t.createdDate) = DATE(:date)")
	long countChallengesByCreatedDate(LocalDateTime date);
}
