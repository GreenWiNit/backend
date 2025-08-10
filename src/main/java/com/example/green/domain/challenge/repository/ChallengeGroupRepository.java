package com.example.green.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupRepository extends JpaRepository<ChallengeGroup, Long> {

	boolean existsByIdAndLeaderId(Long id, Long leaderId);
}
