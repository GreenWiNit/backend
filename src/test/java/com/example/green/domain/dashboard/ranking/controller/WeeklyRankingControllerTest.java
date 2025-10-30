package com.example.green.domain.dashboard.ranking.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.dashboard.rankingmodule.controller.WeeklyRankingController;
import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(WeeklyRankingController.class)
class WeeklyRankingControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private WeeklyRankingService weeklyRankingService;

	@Test
	void 주간_랭킹_조회_성공() {
		// given
		LocalDate weekStart = LocalDate.of(2025, 10, 27);

		TopMemberPointResponseDto topMember1 = new TopMemberPointResponseDto(
			1L, "홍길동", BigDecimal.valueOf(150), 3, weekStart, weekStart.plusDays(6));
		TopMemberPointResponseDto topMember2 = new TopMemberPointResponseDto(
			2L, "김철수", BigDecimal.valueOf(120), 2, weekStart, weekStart.plusDays(6));
		TopMemberPointResponseDto myData = new TopMemberPointResponseDto(
			3L, "이지은", BigDecimal.valueOf(100), 1, weekStart, weekStart.plusDays(6));

		LoadWeeklyRankingResponse mockResponse = new LoadWeeklyRankingResponse(
			List.of(topMember1, topMember2),
			myData);

		PrincipalDetails principal = new PrincipalDetails(
			3L, "google_123456789", "USER", "이지은", "test@test.com");

		when(weeklyRankingService.loadWeeklyRanking(weekStart, 8, 3L))
			.thenReturn(mockResponse);

		// when
		ApiTemplate<LoadWeeklyRankingResponse> response =
			new WeeklyRankingController(weeklyRankingService)
				.getWeeklyRanking(weekStart, principal);

		// then
		assertThat(response.result().topMembers()).hasSize(2);
		assertThat(response.result().topMembers().get(0).nickname()).isEqualTo("홍길동");
		assertThat(response.result().myData().nickname()).isEqualTo("이지은");
		assertThat(response.message()).isEqualTo(
			"주간 환경 챌린저 랭킹 조회에 성공했습니다"); // WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS
	}
}
