package com.example.green.domain.dashboard.ranking.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.dashboard.rankingmodule.controller.WeeklyRankingController;
import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingResponseMessage;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(WeeklyRankingController.class)
class WeeklyRankingControllerTest extends BaseControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WeeklyRankingService weeklyRankingService;

	@Test
	void 주간_전체_랭킹_조회_성공() {
		// given
		LocalDate weekStart = LocalDate.of(2025, 10, 27);

		List<TopMemberPointResponse> topMembers = List.of(
			new TopMemberPointResponse(1L, "홍길동",
				"https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png", 1,
				BigDecimal.valueOf(150), 3, weekStart, weekStart.plusDays(6)),
			new TopMemberPointResponse(2L, "김철수",
				"https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png", 2,
				BigDecimal.valueOf(120), 2, weekStart, weekStart.plusDays(6))
		);

		when(weeklyRankingService.getAllRankData(weekStart, 8))
			.thenReturn(topMembers);

		WeeklyRankingController controller = new WeeklyRankingController(weeklyRankingService);

		// when
		ApiTemplate<List<TopMemberPointResponse>> response =
			controller.getWeeklyRanking(weekStart);

		// then
		assertThat(response.result()).hasSize(2);
		assertThat(response.result().get(0).nickname()).isEqualTo("홍길동");
		assertThat(response.message())
			.isEqualTo(WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS.getMessage());

		verify(weeklyRankingService, times(1)).getAllRankData(weekStart, 8);
	}

	@Test
	void 내_주간_기록_조회_성공() {
		// given
		LocalDate weekStart = LocalDate.of(2025, 10, 27);

		MemberPointResponse myResponse = new MemberPointResponse(
			3L, "이지은", "https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png",
			3, BigDecimal.valueOf(100), 1, weekStart, weekStart.plusDays(6)
		);

		PrincipalDetails principal = new PrincipalDetails(
			3L, "google_123456789", "USER", "이지은", "test@test.com"
		);

		when(weeklyRankingService.getMyData(weekStart, 3L))
			.thenReturn(myResponse);

		WeeklyRankingController controller = new WeeklyRankingController(weeklyRankingService);

		// when
		ApiTemplate<MemberPointResponse> response =
			controller.getMyWeeklyRankingData(weekStart, principal);

		// then
		assertThat(response.result().nickname()).isEqualTo("이지은");
		assertThat(response.result().totalEarned()).isEqualTo(BigDecimal.valueOf(100));
		assertThat(response.message())
			.isEqualTo(WeeklyRankingResponseMessage.LOAD_WEEKLY_MY_DATA_SUCCESS.getMessage());

		verify(weeklyRankingService, times(1)).getMyData(weekStart, 3L);
	}

	@Test
	void 주간_전체_랭킹_조회_JSON_검증() throws Exception {
		// given
		LocalDate weekStart = LocalDate.of(2025, 10, 27);

		List<TopMemberPointResponse> topMembers = List.of(
			new TopMemberPointResponse(
				1L, "홍길동",
				"https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png",
				1,
				BigDecimal.valueOf(150),
				3,
				weekStart,
				weekStart.plusDays(6)
			),
			new TopMemberPointResponse(
				2L, "김철수",
				"https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png",
				2,
				BigDecimal.valueOf(120),
				2,
				weekStart,
				weekStart.plusDays(6)
			)
		);

		when(weeklyRankingService.getAllRankData(weekStart, 8))
			.thenReturn(topMembers);

		// when & then
		mockMvc.perform(get("/api/dashboard/weekly-ranking")
				.param("weekStart", weekStart.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(
				WeeklyRankingResponseMessage.LOAD_WEEKLY_RANKING_SUCCESS.getMessage()
			))
			.andExpect(jsonPath("$.result[0].memberId").value(1L))
			.andExpect(jsonPath("$.result[0].nickname").value("홍길동"))
			.andExpect(jsonPath("$.result[0].totalEarned").value(150))
			.andExpect(jsonPath("$.result[0].verificationCount").value(3))
			.andExpect(jsonPath("$.result[1].memberId").value(2L))
			.andExpect(jsonPath("$.result[1].rank").value(2))
			.andReturn();

		verify(weeklyRankingService, times(1))
			.getAllRankData(weekStart, 8);
	}
}

