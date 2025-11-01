package com.example.green.domain.dashboard.growth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.dashboard.growth.entity.Growth;

public interface GrowthRepository extends JpaRepository<Growth, Long> {

	Optional<Growth> findByMemberId(Long memberId);
}
