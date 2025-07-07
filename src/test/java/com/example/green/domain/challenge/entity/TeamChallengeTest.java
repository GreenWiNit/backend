package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.point.entity.vo.PointAmount;

class TeamChallengeTest {

	private TeamChallenge teamChallenge;
	private PointAmount challengePoint;

	@BeforeEach
	void setUp() {
		challengePoint = PointAmount.of(BigDecimal.valueOf(2000));
		teamChallenge = TeamChallenge.create(
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			5,  // 최대 5팀
			"challenge-image.jpg",
			"팀 챌린지 설명"
		);
	}

	@Test
	void TeamChallenge는_TEAM_타입을_가진다() {
		// when
		ChallengeType type = teamChallenge.getChallengeType();

		// then
		assertThat(type).isEqualTo(ChallengeType.TEAM);
	}

	@Test
	void TeamChallenge_생성_시_challengeCode가_자동으로_생성된다() {
		// when
		String challengeCode = teamChallenge.getChallengeCode();

		// then
		assertThat(challengeCode).isNotNull();
		assertThat(challengeCode).startsWith("CH-T-");
		assertThat(challengeCode).hasSize(21); // CH-T-20250109-1435217 형태 (21자)
		// 날짜 부분 확인
		assertThat(challengeCode).matches("CH-T-\\d{8}-\\d{7}");
	}

	@Test
	void 각_TeamChallenge마다_고유한_challengeCode를_가진다() throws InterruptedException {
		// given
		TeamChallenge challenge1 = TeamChallenge.create(
			"첫 번째 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			5,
			"challenge-image.jpg",
			"첫 번째 팀 챌린지 설명"
		);

		// 동일한 밀리초에 생성되는 것을 방지하기 위해 약간의 지연
		Thread.sleep(1);

		TeamChallenge challenge2 = TeamChallenge.create(
			"두 번째 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			3,
			"challenge-image.jpg",
			"두 번째 팀 챌린지 설명"
		);

		// when
		String code1 = challenge1.getChallengeCode();
		String code2 = challenge2.getChallengeCode();

		// then
		// 현재는 시간 기반으로 생성되어 거의 항상 고유하지만,
		// 향후 시퀀스 테이블이나 Redis로 변경 시 완전한 고유성 보장
		assertThat(code1).isNotEqualTo(code2);
		assertThat(code1).startsWith("CH-T-");
		assertThat(code2).startsWith("CH-T-");
	}

	@Test
	void 팀_챌린지_코드가_시간_기반으로_올바르게_생성된다() {
		// when
		TeamChallenge challenge = TeamChallenge.create(
			"시간 기반 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7),
			10,
			"challenge-image.jpg",
			"시간 기반 팀 챌린지 설명"
		);

		// then
		String challengeCode = challenge.getChallengeCode();
		String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		// 형식 검증: CH-T-yyyyMMdd-HHmmsss (시분초 + 밀리초 마지막 자리)
		assertThat(challengeCode).matches("CH-T-\\d{8}-\\d{7}");
		assertThat(challengeCode).contains(today); // 오늘 날짜 포함
		assertThat(challengeCode).hasSize(21);

		// 타입 확인
		assertThat(challengeCode).startsWith("CH-T-");

		// 시간 부분이 숫자인지 확인 (마지막 7자리)
		String timePart = challengeCode.substring(challengeCode.length() - 7);
		assertThat(timePart).matches("\\d{7}");
	}

	@Test
	void 그룹_추가_시_챌린지_그룹_리스트에_추가되고_팀_카운트가_증가한다() {
		// given
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"테스트 그룹 1",
			GroupStatus.RECRUITING,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(8),
			10,
			"서울시 강남구",
			"테스트 그룹 설명",
			"https://openchat.example.com"
		);

		// when
		teamChallenge.addChallengeGroup(group);

		// then
		assertThat(teamChallenge.getChallengeGroups()).contains(group);
		assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(1);
		assertThat(group.getTeamChallenge()).isEqualTo(teamChallenge);
	}

	@Test
	void 여러_그룹_추가_시_팀_카운트가_올바르게_증가한다() {
		// given
		TeamChallengeGroup group1 = TeamChallengeGroup.create(
			"테스트 그룹 1",
			GroupStatus.RECRUITING,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(8),
			10,
			null, null, null
		);
		TeamChallengeGroup group2 = TeamChallengeGroup.create(
			"테스트 그룹 2",
			GroupStatus.RECRUITING,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(8),
			10,
			null, null, null
		);
		TeamChallengeGroup group3 = TeamChallengeGroup.create(
			"테스트 그룹 3",
			GroupStatus.RECRUITING,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(8),
			10,
			null, null, null
		);

		// when
		teamChallenge.addChallengeGroup(group1);
		teamChallenge.addChallengeGroup(group2);
		teamChallenge.addChallengeGroup(group3);

		// then
		assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(3);
		assertThat(teamChallenge.getChallengeGroups()).hasSize(3);
	}

	@Test
	void 그룹_제거_시_챌린지_그룹_리스트에서_제거되고_팀_카운트가_감소한다() {
		// given
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"테스트 그룹",
			GroupStatus.RECRUITING,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(8),
			10,
			null, null, null
		);
		teamChallenge.addChallengeGroup(group);

		// when
		teamChallenge.removeChallengeGroup(group);

		// then
		assertThat(teamChallenge.getChallengeGroups()).doesNotContain(group);
		assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(0);
		assertThat(group.getTeamChallenge()).isNull();
	}

	@Test
	void maxTeamCount가_null이면_항상_팀_추가_가능하다() {
		// given
		TeamChallenge challenge = TeamChallenge.create(
			"무제한 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			null,  // 제한 없음
			"challenge-image.jpg",
			"무제한 챌린지 설명"
		);

		// when
		boolean canAddTeam = challenge.canAddGroup();

		// then
		assertTrue(canAddTeam);
	}

	@Test
	void 현재_팀_수가_최대_팀_수보다_적으면_팀_추가_가능하다() {
		// given (현재 0팀, 최대 5팀)

		// when
		boolean canAddTeam = teamChallenge.canAddGroup();

		// then
		assertTrue(canAddTeam);
	}

	@Test
	void 현재_팀_수가_최대_팀_수와_같으면_팀_추가_불가능하다() {
		// given - 3개 그룹을 미리 추가
		TeamChallenge challenge = TeamChallenge.create(
			"만석 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			3,  // 최대 3팀
			"challenge-image.jpg",
			"만석 챌린지 설명"
		);

		// 3개 그룹을 추가하여 만석 상태로 만듦
		for (int i = 0; i < 3; i++) {
			TeamChallengeGroup group = TeamChallengeGroup.create(
				"그룹 " + (i + 1),
				GroupStatus.RECRUITING,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(8),
				10,
				null, null, null
			);
			challenge.addChallengeGroup(group);
		}

		// when
		boolean canAddTeam = challenge.canAddGroup();

		// then
		assertFalse(canAddTeam);
	}

	@Test
	void 최대_팀_수에_도달했는지_올바르게_확인한다() {
		// given
		TeamChallenge challenge = TeamChallenge.create(
			"만석 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			2,  // 최대 2팀
			"challenge-image.jpg",
			"만석 챌린지 설명"
		);

		// 2개 그룹을 추가하여 만석 상태로 만듦
		for (int i = 0; i < 2; i++) {
			TeamChallengeGroup group = TeamChallengeGroup.create(
				"그룹 " + (i + 1),
				GroupStatus.RECRUITING,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(8),
				10,
				null, null, null
			);
			challenge.addChallengeGroup(group);
		}

		// when
		boolean isMaxReached = challenge.isMaxGroupCountReached();

		// then
		assertTrue(isMaxReached);
	}

	@Test
	void maxTeamCount가_null이면_최대_팀_수에_도달하지_않은_것으로_판단한다() {
		// given
		TeamChallenge challenge = TeamChallenge.create(
			"무제한 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			null,  // 제한 없음
			"challenge-image.jpg",
			"무제한 챌린지 설명"
		);

		// when
		boolean isMaxReached = challenge.isMaxGroupCountReached();

		// then
		assertFalse(isMaxReached);
	}
}
