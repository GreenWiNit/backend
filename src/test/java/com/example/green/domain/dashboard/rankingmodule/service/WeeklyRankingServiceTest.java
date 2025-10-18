package com.example.green.domain.dashboard.rankingmodule.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.dto.WeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.repository.MemberRepository;

class WeeklyRankingServiceTest {

	@Mock
	private WeeklyRankingRepository weeklyRankingRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private com.querydsl.jpa.impl.JPAQueryFactory queryFactory;

	@InjectMocks
	private WeeklyRankingService weeklyRankingService;

	private LocalDate weekStart;
	private Long systemUserId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		weekStart = LocalDate.of(2025, 10, 18);
		systemUserId = 1L;
	}

	@Test
	void calculateAndSaveWeeklyRanking_savesOnlyIfNotExists() {
		// 이미 랭킹이 존재하면 아무 동작도 안 함
		when(weeklyRankingRepository.findByWeekStart(weekStart)).thenReturn(List.of(new WeeklyRanking()));

		weeklyRankingService.calculateAndSaveWeeklyRanking(weekStart, 5, systemUserId);

		verify(weeklyRankingRepository, never()).save(any());
	}

	@Test
	void viewWeeklyRanking_returnsTopAndCurrentMember() {
		// given
		WeeklyRanking r1 = WeeklyRanking.builder()
			.memberId(1L)
			.memberName("Alice")
			.totalEarned(BigDecimal.valueOf(100))
			.certificationCount(5)
			.rank("1")
			.weekStart(weekStart)
			.build();

		WeeklyRanking r2 = WeeklyRanking.builder()
			.memberId(2L)
			.memberName("Bob")
			.totalEarned(BigDecimal.valueOf(80))
			.certificationCount(3)
			.rank("2")
			.weekStart(weekStart)
			.build();

		when(weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(weekStart, 5))
			.thenReturn(List.of(r1, r2));

		// when
		WeeklyRankingResponse response = weeklyRankingService.viewWeeklyRanking(weekStart, 1L, 5);

		// then
		List<TopMemberPointResponseDto> topList = response.topMembers();
		assertThat(topList).hasSize(2);
		assertThat(topList.get(0).nickname()).isEqualTo("Alice");

		TopMemberPointResponseDto myData = response.myData();
		assertThat(myData.nickname()).isEqualTo("Alice");
		assertThat(myData.totalEarned()).isEqualByComparingTo(BigDecimal.valueOf(100));
	}

	@Test
	void viewWeeklyRanking_returnsUnknownIfCurrentMemberNotInTop() {
		WeeklyRanking r1 = WeeklyRanking.builder()
			.memberId(2L)
			.memberName("Bob")
			.totalEarned(BigDecimal.valueOf(80))
			.certificationCount(3)
			.rank("1")
			.weekStart(weekStart)
			.build();

		when(weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(weekStart, 5))
			.thenReturn(List.of(r1));

		WeeklyRankingResponse response = weeklyRankingService.viewWeeklyRanking(weekStart, 1L, 5);

		TopMemberPointResponseDto myData = response.myData();
		assertThat(myData.nickname()).isEqualTo("Unknown");
		assertThat(myData.totalEarned()).isEqualByComparingTo(BigDecimal.ZERO);
	}
}
