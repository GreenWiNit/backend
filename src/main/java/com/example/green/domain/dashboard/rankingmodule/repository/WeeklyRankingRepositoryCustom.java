package com.example.green.domain.dashboard.rankingmodule.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.querydsl.core.Tuple;

public interface WeeklyRankingRepositoryCustom {
	List<Tuple> findTopMembersOfWeek(LocalDate weekStart, int topN);

	Optional<WeeklyRanking> myData(LocalDate weekStart, Long memberId);

	List<WeeklyRanking> findTopNByWeekStart(LocalDate weekStart, int topN);

	List<WeeklyRanking> findAllRankings();
}

