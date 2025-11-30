package com.example.green.domain.dashboard.rankingmodule.service;

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
		weeklyRankingService.updateWeeklyRanks();
	}
}
