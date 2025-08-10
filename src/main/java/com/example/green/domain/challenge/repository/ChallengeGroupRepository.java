package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

	@Query("SELECT COUNT(g) FROM ChallengeGroup g WHERE DATE(g.createdDate) = DATE(:date)")
	long countGroupsByCreatedDate(LocalDateTime date);
}
