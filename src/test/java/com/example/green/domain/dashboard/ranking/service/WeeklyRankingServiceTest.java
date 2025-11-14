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

import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.dashboard.rankingmodule.service.WeeklyRankingService;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

class WeeklyRankingServiceTest {

	@Mock
	private WeeklyRankingRepository weeklyRankingRepository;

	@Mock
	private MemberRepository memberRepository;

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
	@DisplayName("상위 랭킹 조회 성공")
	void 상위_랭킹_조회_성공() {
		// given
		when(weeklyRankingRepository.findTopNByWeekStart(any(), anyInt()))
			.thenReturn(List.of(rank1, rank2));

		// when
		List<TopMemberPointResponse> response = weeklyRankingService.getAllRankData(weekStart, 2);

		// then
		assertThat(response).hasSize(2);
		assertThat(response.get(0).nickname()).isEqualTo("홍길동");
		assertThat(response.get(1).nickname()).isEqualTo("김철수");

		verify(weeklyRankingRepository, times(1)).findTopNByWeekStart(any(), anyInt());
	}

	@Test
	void 내_주간_기록_조회_성공() {
		// given
		when(weeklyRankingRepository.myData(weekStart, 1L))
			.thenReturn(Optional.of(rank1));

		// when
		MemberPointResponse response = weeklyRankingService.getMyData(weekStart, 1L);

		// then
		assertThat(response.nickname()).isEqualTo("홍길동");
		assertThat(response.totalEarned()).isEqualTo(BigDecimal.valueOf(120L));

		verify(weeklyRankingRepository, times(1)).myData(weekStart, 1L);
	}

	@Test
	void 내_주간_기록_없는_경우의_조회_0포인트로_반환() {
		// given
		when(weeklyRankingRepository.myData(weekStart, 1L))
			.thenReturn(Optional.empty());

		Member member = Member.create("memberKey-123", "홍길동", "test@test.com", "nickname");

		when(memberRepository.findById(1L))
			.thenReturn(Optional.of(member));

		// when
		MemberPointResponse response = weeklyRankingService.getMyData(weekStart, 1L);

		// then
		assertThat(response.nickname()).isEqualTo("홍길동");
		assertThat(response.totalEarned()).isEqualTo(BigDecimal.ZERO);
		assertThat(response.verificationCount()).isEqualTo(0);

		verify(weeklyRankingRepository, times(1)).myData(weekStart, 1L);
		verify(memberRepository, times(1)).findById(1L);
	}
}
