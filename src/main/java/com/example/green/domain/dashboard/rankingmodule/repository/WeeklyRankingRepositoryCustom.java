package com.example.green.domain.dashboard.rankingmodule.repository;

import java.time.LocalDate;
import java.util.List;

import com.querydsl.core.Tuple;

public interface WeeklyRankingRepositoryCustom {
	List<Tuple> findTopMembersOfWeek(LocalDate weekStart, int topN);
}
