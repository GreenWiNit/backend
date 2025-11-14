package com.example.green.domain.dashboard.rankingmodule.controller.docs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "대시보드 랭킹 API", description = "상위 8명의 주간 단위 랭킹과 로그인 사용자의 주간 랭킹을 조회합니다.")
public interface WeeklyRankingControllerDocs {

	@WeeklyRankingCreateDocs
	ApiTemplate<List<TopMemberPointResponse>> getWeeklyRanking(
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
	);

	@WeeklyRankingMyDataCreateDocs
	ApiTemplate<MemberPointResponse> getMyWeeklyRankingData(
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	);
}
