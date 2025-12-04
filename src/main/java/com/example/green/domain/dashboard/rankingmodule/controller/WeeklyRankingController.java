package com.example.green.domain.dashboard.rankingmodule.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.rankingmodule.controller.docs.WeeklyRankingControllerDocs;
import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingResponseMessage;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/weekly-ranking")
public class WeeklyRankingController implements WeeklyRankingControllerDocs {

	private final WeeklyRankingService weeklyRankingService;

	@PublicApi(reason = "로그인 하지않아도 전체 순위 데이터를 조회 할 수 있습니다")
	@GetMapping
	public ApiTemplate<List<TopMemberPointResponse>> getWeeklyRanking(
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
	) {

		List<TopMemberPointResponse> response =
			weeklyRankingService.getAllRankData(weekStart);

		return ApiTemplate.ok(WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS, response);
	}

	@AuthenticatedApi(reason = "로그인한 사용자는 자신의 이번주 포인트와 챌린지 수를 조회할 수 있습니다")
	@GetMapping("/my")
	public ApiTemplate<MemberPointResponse> getMyWeeklyRankingData(
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();

		MemberPointResponse response =
			weeklyRankingService.getMyData(weekStart, memberId);

		return ApiTemplate.ok(WeeklyRankingResponseMessage.LOAD_WEEKLY_MY_DATA_SUCCESS, response);
	}
}
