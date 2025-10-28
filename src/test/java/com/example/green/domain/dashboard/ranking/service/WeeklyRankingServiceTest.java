package com.example.green.domain.dashboard.ranking.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.exception.WeeklyRankingException;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingExceptionMessage;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;

public class WeeklyRankingServiceTest {

	@Mock
	private WeeklyRankingRepository weeklyRankingRepository;

	@InjectMocks
	private WeeklyRankingService weeklyRankingService;

	private WeeklyRanking rank1;
	private WeeklyRanking rank2;
	private LocalDate weekStart;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		weekStart = LocalDate.of(2025, 10, 27);

		rank1 = WeeklyRanking.builder()
			.memberId(1L)
			.memberName("홍길동")
			.totalPoint(BigDecimal.valueOf(120L))
			.certificationCount(3)
			.rank(1)
			.weekStart(weekStart)
			.weekEnd(weekStart.plusDays(6))
			.build();

		rank2 = WeeklyRanking.builder()
			.memberId(2L)
			.memberName("김철수")
			.totalPoint(BigDecimal.valueOf(100L))
			.certificationCount(2)
			.rank(2)
			.weekStart(weekStart)
			.weekEnd(weekStart.plusDays(6))
			.build();
	}

	@Test
	@DisplayName("상위 랭킹과 내 랭킹 조회 성공한 경우")
	void loadWeeklyRankingSuccess() {
		//given
		when(weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(any(), anyInt()))
			.thenReturn(List.of(rank1, rank2));
		when(weeklyRankingRepository.myData(weekStart, 1L))
			.thenReturn(Optional.of(rank1));

		//when
		LoadWeeklyRankingResponse response = weeklyRankingService.loadWeeklyRanking(weekStart, 2, 1L);

		//then
		assertThat(response.topMembers()).hasSize(2);
		assertThat(response.myData().nickname()).isEqualTo("홍길동");

		verify(weeklyRankingRepository, times(1)).findTopNByWeekStartOrderByRankAsc(any(), anyInt());
		verify(weeklyRankingRepository, times(1)).myData(weekStart, 1L);

	}

	@Test
	@DisplayName("내 랭킹 데이터가 없는 경우 예외 발생")
	void loadWeeklyRanking_NotFoundUser() {
		//given
		when(weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(any(), anyInt()))
			.thenReturn(List.of(rank1, rank2));
		when(weeklyRankingRepository.myData(weekStart, 3L))
			.thenReturn(Optional.empty());

		//when . then
		assertThatThrownBy(() -> weeklyRankingService.loadWeeklyRanking(weekStart, 2, 3L))
			.isInstanceOf(WeeklyRankingException.class)
			.hasMessage(WeeklyRankingExceptionMessage.NOT_FOUND_USER.getMessage());
	}
}
