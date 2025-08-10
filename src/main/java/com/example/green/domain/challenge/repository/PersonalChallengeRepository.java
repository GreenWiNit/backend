package com.example.green.domain.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;

public interface PersonalChallengeRepository extends JpaRepository<PersonalChallenge, Long> {
}
