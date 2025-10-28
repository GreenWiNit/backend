package com.example.green.domain.dashboard.rankingmodule.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class WeeklyRankingController {

	private final WeeklyRankingService weeklyRankingService;

	@AuthenticatedApi(reason = "")
	@GetMapping("/weekly-ranking")
	public LoadWeeklyRankingResponse getWeeklyRanking(
		@RequestParam("weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		Long currentMemberId = principal.getMemberId();
		int topN = 8;
		return weeklyRankingService.loadWeeklyRanking(weekStart, topN, currentMemberId);
	}
}
