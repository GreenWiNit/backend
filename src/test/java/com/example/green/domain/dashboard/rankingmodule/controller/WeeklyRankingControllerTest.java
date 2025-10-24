package com.example.green.domain.dashboard.rankingmodule.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.dto.WeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(WeeklyRankingController.class)
class WeeklyRankingControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private WeeklyRankingService weeklyRankingService;

	private LocalDate weekStart;

	@BeforeEach
	void init() {
		weekStart = LocalDate.of(2025, 10, 18);
	}

	@Test
	void viewWeeklyRanking_returnsTopAndCurrentMember() {
		TopMemberPointResponseDto top1 = new TopMemberPointResponseDto("Alice", BigDecimal.valueOf(100), 5);
		TopMemberPointResponseDto top2 = new TopMemberPointResponseDto("Bob", BigDecimal.valueOf(80), 3);
		TopMemberPointResponseDto myData = new TopMemberPointResponseDto("Alice", BigDecimal.valueOf(100), 5);

		WeeklyRankingResponse mockResponse = new WeeklyRankingResponse(List.of(top1, top2), myData);

		when(weeklyRankingService.viewWeeklyRanking(weekStart, 1L, 5))
			.thenReturn(mockResponse);

		RestAssuredMockMvc.given()
			.param("weekStart", weekStart.toString())
			.param("topN", 8)
			.get("/api/dashboard/weekly-ranking")
			.then()
			.statusCode(200)
			.body("topMembers.size()", equalTo(2))
			.body("topMembers[0].nickname", equalTo("Alice"))
			.body("myData.nickname", equalTo("Alice"))
			.body("myData.totalEarned", equalTo(100));
	}

	@Test
	void viewWeeklyRanking_returnsUnknownIfCurrentMemberNotInTop() {
		TopMemberPointResponseDto top1 = new TopMemberPointResponseDto("Bob", BigDecimal.valueOf(80), 3);
		TopMemberPointResponseDto myData = new TopMemberPointResponseDto("Unknown", BigDecimal.ZERO, 0);

		WeeklyRankingResponse mockResponse = new WeeklyRankingResponse(List.of(top1), myData);

		when(weeklyRankingService.viewWeeklyRanking(weekStart, 1L, 5))
			.thenReturn(mockResponse);

		RestAssuredMockMvc.given()
			.param("weekStart", weekStart.toString())
			.param("topN", "8")
			.get("/api/dashboard/weekly-ranking")
			.then()
			.statusCode(200)
			.body("myData.nickname", equalTo("Unknown"))
			.body("myData.totalEarned", equalTo(0));
	}
}
