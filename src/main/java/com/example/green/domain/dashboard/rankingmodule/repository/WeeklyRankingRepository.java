package com.example.green.domain.dashboard.rankingmodule.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;

public interface WeeklyRankingRepository extends JpaRepository<WeeklyRanking, Long>, WeeklyRankingRepositoryCustom {
	List<WeeklyRanking> findByWeekStart(LocalDate weekStart);

	List<WeeklyRanking> findTopNByWeekStartOrderByRankAsc(LocalDate weekStart, int n);

}
