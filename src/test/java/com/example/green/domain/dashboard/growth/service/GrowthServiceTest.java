package com.example.green.domain.dashboard.growth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.repository.GrowthRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.infra.client.PointClient;

class GrowthServiceTest {

	@Mock
	private GrowthRepository growthRepository;

	@Mock
	private PointClient pointClient;

	private GrowthCalculateService calculateService; // 실제 객체
	private GrowthService growthService; // 테스트 대상 서비스
	@Mock
	private MemberRepository memberRepository;

	private Growth growth;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		Member member = Member.create("memberKey-123", "홍길동", "test@test.com", "nickname");

		growth = Growth.create(Level.SOIL, BigDecimal.valueOf(150L), BigDecimal.valueOf(500L), Level.SPROUT, member);
		growth.setMember(member);
		growth.setProgress(Level.SOIL, BigDecimal.ZERO, BigDecimal.valueOf(250L), Level.SPROUT);

		calculateService = new GrowthCalculateService(growthRepository, pointClient);

		growthService = new GrowthService(growthRepository, calculateService, memberRepository);
	}

	@Test
	void 사용자_ID와_총포인트로_식물_성장_계산한다() {
		// given
		Long memberId = 1L;
		when(growthRepository.findByMemberId(memberId)).thenReturn(Optional.of(growth));
		when(pointClient.getTotalPoints(memberId)).thenReturn(BigDecimal.valueOf(150L));

		// when
		calculateService.calculateMemberGrowth(memberId);

		// then
		assertThat(growth.getGoalLevel()).isEqualTo(Level.SPROUT);
		assertThat(growth.getProgress()).isGreaterThan(BigDecimal.ZERO);
	}

	@Test
	void 레벨별_다음_레벨과_진행률_반환() {
		// given
		Long memberId = 1L;
		Member member = Member.create("memberKey-123", "홍길동", "test@test.com", "nickname");

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(growthRepository.findByMemberId(memberId)).thenReturn(Optional.of(growth));
		when(pointClient.getTotalPoints(memberId)).thenReturn(BigDecimal.valueOf(550L));

		// when
		LoadGrowthResponse growthResponse = growthService.loadGrowth(memberId);

		// then
		assertThat(growthResponse.currentLevel()).isEqualTo(Level.SPROUT);
		assertThat(growthResponse.goalLevel()).isEqualTo(Level.SAPLING);
	}
}
