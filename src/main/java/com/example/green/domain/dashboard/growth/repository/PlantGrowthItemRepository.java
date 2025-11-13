package com.example.green.domain.dashboard.growth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;

public interface PlantGrowthItemRepository
	extends JpaRepository<PlantGrowthItem, Long>, PlantGrowthItemRepositoryCustom {
}
