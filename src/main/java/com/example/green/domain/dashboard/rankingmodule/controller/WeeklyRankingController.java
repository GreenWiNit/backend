package com.example.green.domain.dashboard.rankingmodule.controller;

import java.time.LocalDate;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.rankingmodule.controller.message.WeeklyRankingResponseMessage;
import com.example.green.domain.dashboard.rankingmodule.dto.WeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.api.ApiTemplate;
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
	public ApiTemplate<WeeklyRankingResponse> getweeklyRanking(
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		Long memberId = currentUser.getMemberId();
		WeeklyRankingResponse weeklyRankingResponse = weeklyRankingService.calculateWeeklyRanking(LocalDate.now(), 8,
			memberId);

		log.info("[WEEKLY-RANKING] 이번주 가장 활발한 환경 챌린저 정보 조회{}", weeklyRankingResponse);

		return ApiTemplate.ok(WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS, weeklyRankingResponse);
	}
	
}
