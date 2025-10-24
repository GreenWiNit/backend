package com.example.green.domain.dashboard.rankingmodule.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;

@Repository
public interface WeeklyRankingRepository extends JpaRepository<WeeklyRanking, Long> {
	List<WeeklyRanking> findByWeekStart(LocalDate weekStart);

	List<WeeklyRanking> findTopNByWeekStartOrderByRankAsc(LocalDate weekStart, int n);

}
