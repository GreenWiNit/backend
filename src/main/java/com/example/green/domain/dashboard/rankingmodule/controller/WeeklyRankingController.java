package com.example.green.domain.dashboard.rankingmodule.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingResponseMessage;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/weekly-ranking")
public class WeeklyRankingController implements WeeklyRankingControllerDocs {

	private final WeeklyRankingService weeklyRankingService;

	@GetMapping
	public ApiTemplate<LoadWeeklyRankingResponse> getWeeklyRanking(
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		Long currentMemberId = principal.getMemberId();
		int topN = 8;

		LoadWeeklyRankingResponse response =
			weeklyRankingService.loadWeeklyRanking(weekStart, topN, currentMemberId);

		return ApiTemplate.ok(WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS, response);
	}
}
