package com.example.green.domain.dashboard.rankingmodule.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyRankingScheduler {

	private final WeeklyRankingService weeklyRankingService;

	/**
	 * 매주 월요일 00:00시 지난 주 랭킹 계산
	 */
	@Scheduled(cron = "0 0 17 * * THU", zone = "Asia/Seoul")
	public void calculateWeeklyRankingBatch() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		// 오늘이 월요일 → 지난주 계산
		LocalDate weekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
		LocalDate weekEnd = today.minusWeeks(1).with(DayOfWeek.SUNDAY);

		weeklyRankingService.updateWeeklyRanks(weekStart, weekEnd);
	}
}
