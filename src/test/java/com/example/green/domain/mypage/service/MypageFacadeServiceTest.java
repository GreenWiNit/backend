package com.example.green.domain.mypage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.mypage.client.PointTotalGetClient;
import com.example.green.domain.mypage.dto.MypageMainResponseDto;
import com.example.green.domain.mypage.exception.MypageException;

@ExtendWith(MockitoExtension.class)
class MypageFacadeServiceTest {

	private final Long memberId = 123L;

	// @Mock
	// private ChallengeCountGetClient challengeCountGetClient;
	@Mock
	private PointTotalGetClient pointTotalGetClient;
	@InjectMocks
	private MypageFacadeService service;

	@BeforeEach
	void setUp() {
		// when(challengeCountGetClient.getChallengeCount(memberId)).thenReturn(5);
	}

	@ParameterizedTest(name = "포인트={0} → 예상레벨={1}")
	@CsvSource({
		"0,       1",
		"999,     1",
		"1000,    2",
		"1999,    2",
		"2000,    3",
		"3999,    3",
		"4000,    4",
		"9999,    4",
		"10000,  10",
		"15000,  10"
	})
	void 마이페이지메인_경계값_테스트(String points, int expectedLevel) {
		// given
		BigDecimal totalPoints = new BigDecimal(points);
		when(pointTotalGetClient.getTotalPoints(memberId)).thenReturn(totalPoints);

		// when
		MypageMainResponseDto dto = service.getMypageMain(memberId);

		// then
		// (1) 챌린지 카운트는 여전히 0
		assertThat(dto.userChallengeCount()).isZero();

		// (2) 받은 포인트가 그대로 노출
		assertThat(dto.userTotalPoints()).isEqualByComparingTo(totalPoints);

		// (3) 경계값에 따른 레벨 검증
		assertThat(dto.userLevel())
			.as("포인트 %s 일 때 레벨은 %d 여야 한다", points, expectedLevel)
			.isEqualTo(expectedLevel);
	}

	@Test
	void 마이페이지메인_포인트_값이_null일_때_예외를_반환한다() {
		// given
		when(pointTotalGetClient.getTotalPoints(memberId)).thenReturn(null);

		// then
		assertThatThrownBy(() -> service.getMypageMain(memberId))
			.isInstanceOf(MypageException.class)
			.hasMessageContaining("사용자의 총 포인트는 NULL 일 수 없습니다.");
	}

}