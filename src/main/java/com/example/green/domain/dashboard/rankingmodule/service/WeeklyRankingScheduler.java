package com.example.green.domain.dashboard.rankingmodule.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
	@Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
	public void calculateWeeklyRankingBatch() {
		LocalDate lastWeekStart = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
		int topN = 8; // 상위 N명

		weeklyRankingService.calculateAndSaveWeeklyRanking(lastWeekStart, topN);
	}
}
