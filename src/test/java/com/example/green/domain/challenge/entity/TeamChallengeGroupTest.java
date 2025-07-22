package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;

class TeamChallengeGroupTest {

	private TeamChallengeGroup teamChallengeGroup;
	private TeamChallenge teamChallenge;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// TeamChallenge 먼저 생성
		teamChallenge = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now),
			"테스트 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		teamChallengeGroup = TeamChallengeGroup.create(
			"TEST-GROUP-001",
			"테스트 그룹",
			now.minusHours(1),  // 1시간 전 시작
			now.plusHours(1),   // 1시간 후 종료
			10,  // 최대 10명
			GroupAddress.of("서울시 강남구"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			teamChallenge
		);

		// 현재 참가자 수를 5명으로 설정
		for (int i = 0; i < 5; i++) {
			teamChallengeGroup.addParticipant();
		}
	}

	@Test
	void 참가자_증가_시_currentParticipants가_1_증가한다() {
		// given
		int initialCount = teamChallengeGroup.getCurrentParticipants();

		// when
		teamChallengeGroup.addParticipant();

		// then
		assertThat(teamChallengeGroup.getCurrentParticipants()).isEqualTo(initialCount + 1);
	}

	@Test
	void 참가자_감소_시_currentParticipants가_1_감소한다() {
		// given
		int initialCount = teamChallengeGroup.getCurrentParticipants();

		// when
		teamChallengeGroup.removeParticipant();

		// then
		assertThat(teamChallengeGroup.getCurrentParticipants()).isEqualTo(initialCount - 1);
	}

	@Test
	void currentParticipants가_0일_때_참가자_감소해도_0을_유지한다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"TEAM-002",
			"테스트 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			10,
			GroupAddress.of("서울시 강남구"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			teamChallenge
		);
		// currentParticipants는 생성 시 0으로 초기화됨

		// when
		group.removeParticipant();

		// then
		assertThat(group.getCurrentParticipants()).isEqualTo(0);
	}

	@Test
	void 최대_인원에_도달하면_true를_반환한다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"TEAM-003",
			"만석 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			5,   // 최대 5명
			GroupAddress.of("서울시 강남구"),
			"만석 그룹 설명",
			"https://openchat.example.com",
			teamChallenge
		);

		// 5명을 추가하여 만석으로 만듦
		for (int i = 0; i < 5; i++) {
			group.addParticipant();
		}

		// when
		boolean isMaxReached = group.isMaxParticipantsReached();

		// then
		assertTrue(isMaxReached);
	}

	@Test
	void maxParticipants가_null이면_최대_인원에_도달하지_않은_것으로_판단한다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"group name",
			testNow.minusHours(1),
			testNow.plusHours(1),
			null, // 제한 없음
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// 100명을 추가
		for (int i = 0; i < 100; i++) {
			group.addParticipant();
		}

		// when
		boolean isMaxReached = group.isMaxParticipantsReached();

		// then
		assertFalse(isMaxReached);
	}

	@Test
	void 최대인원_미달이고_종료시간_전이면_참가_가능하다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"group name",
			testNow.minusHours(1),
			testNow.plusHours(1),  // 종료시간 전
			10,  // 최대 10명
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// 5명만 추가 (여유 있음)
		for (int i = 0; i < 5; i++) {
			group.addParticipant();
		}

		// when
		boolean canParticipate = group.canParticipate(testNow);

		// then
		assertTrue(canParticipate);
	}

	@Test
	void 최대_인원에_도달하면_참가할_수_없다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"만석 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			5,   // 최대 5명
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// 5명을 추가하여 만석으로 만듦
		for (int i = 0; i < 5; i++) {
			group.addParticipant();
		}

		// when
		boolean canParticipate = group.canParticipate(testNow);

		// then
		assertFalse(canParticipate);
	}

	@Test
	void 종료_시간이_지나면_참가할_수_없다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"종료된 그룹",
			testNow.minusHours(2),
			testNow.minusHours(1),  // 1시간 전 종료
			10,
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		boolean canParticipate = group.canParticipate(testNow);

		// then
		assertFalse(canParticipate);
	}

	@Test
	void 현재_시간이_그룹_기간_내에_있으면_활성_상태이다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"활성 그룹",
			testNow.minusHours(1),  // 1시간 전 시작
			testNow.plusHours(1),   // 1시간 후 종료
			10,
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		boolean isActive = group.isActive(testNow);

		// then
		assertTrue(isActive);
	}

	@Test
	void 시작_시간_이전이면_활성_상태가_아니다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"미래 그룹",
			testNow.plusHours(1),  // 1시간 후 시작
			testNow.plusHours(2),
			10,
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		boolean isActive = group.isActive(testNow);

		// then
		assertFalse(isActive);
	}

	@Test
	void 종료_시간_이후면_활성_상태가_아니다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"종료된 그룹",
			testNow.minusHours(2),
			testNow.minusHours(1),  // 1시간 전 종료
			10,
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		boolean isActive = group.isActive(testNow);

		// then
		assertFalse(isActive);
	}

	@Test
	void 최대_참가자_수가_0_이하이면_ChallengeException이_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeGroup.create(
			"team code",
			"잘못된 그룹",
			LocalDateTime.now().minusHours(1),
			LocalDateTime.now().plusHours(1),
			0,  // 잘못된 값
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
	}

	@Test
	void 최대_참가자_수가_음수이면_ChallengeException이_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeGroup.create(
			"team code",
			"잘못된 그룹",
			LocalDateTime.now().minusHours(1),
			LocalDateTime.now().plusHours(1),
			-10,  // 잘못된 값
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
	}

	@Test
	void 최대_인원_도달_시_상태가_COMPLETED로_변경된다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"테스트 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			5,   // 최대 5명
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		// 5명을 추가하여 만석으로 만듦
		for (int i = 0; i < 5; i++) {
			group.addParticipant();
		}

		// then
		assertThat(group.getGroupStatus()).isEqualTo(GroupStatus.COMPLETED);
	}

	@Test
	void 최대_인원_미달_시_상태가_RECRUITING으로_유지된다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"테스트 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			10,   // 최대 10명
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// when
		// 5명만 추가
		for (int i = 0; i < 5; i++) {
			group.addParticipant();
		}

		// then
		assertThat(group.getGroupStatus()).isEqualTo(GroupStatus.RECRUITING);
	}

	@Test
	void 최대_인원_초과_시_참가자_추가에서_예외가_발생한다() {
		// given
		LocalDateTime testNow = now;
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			"제한된 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			2,   // 최대 2명
			GroupAddress.of("서울시 강남구"), null, null,
			teamChallenge
		);

		// 2명을 추가하여 만석으로 만듦
		group.addParticipant();
		group.addParticipant();

		// when & then
		assertThatThrownBy(() -> group.addParticipant())
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.GROUP_IS_FULL);
	}

	@Test
	void 그룹_정보를_정상적으로_수정할_수_있다() {
		// given
		String newGroupName = "수정된 그룹명";
		GroupAddress newGroupAddress = GroupAddress.of("서울시 서초구 강남대로 456", "서초동 타워 3층");
		String newDescription = "수정된 그룹 설명";
		String newOpenChatUrl = "https://updated-chat.com";
		LocalDateTime newBeginDateTime = now.plusHours(2);
		LocalDateTime newEndDateTime = now.plusHours(4);
		Integer newMaxParticipants = 15;

		// when
		teamChallengeGroup.update(
			newGroupName,
			newGroupAddress,
			newDescription,
			newOpenChatUrl,
			newBeginDateTime,
			newEndDateTime,
			newMaxParticipants
		);

		// then
		assertThat(teamChallengeGroup.getGroupName()).isEqualTo(newGroupName);
		assertThat(teamChallengeGroup.getGroupAddress()).isEqualTo(newGroupAddress);
		assertThat(teamChallengeGroup.getGroupDescription()).isEqualTo(newDescription);
		assertThat(teamChallengeGroup.getOpenChatUrl()).isEqualTo(newOpenChatUrl);
		assertThat(teamChallengeGroup.getGroupBeginDateTime()).isEqualTo(newBeginDateTime);
		assertThat(teamChallengeGroup.getGroupEndDateTime()).isEqualTo(newEndDateTime);
		assertThat(teamChallengeGroup.getMaxParticipants()).isEqualTo(newMaxParticipants);
	}

	@Test
	void 그룹명이_빈_문자열인_경우_수정_시_예외가_발생한다() {
		// given
		String invalidGroupName = "";

		// when & then
		assertThatThrownBy(() -> teamChallengeGroup.update(
			invalidGroupName,
			GroupAddress.of("서울시 서초구"),
			"설명",
			"https://chat.com",
			now.plusHours(1),
			now.plusHours(2),
			10
		)).isInstanceOf(BusinessException.class);
	}

	@Test
	void 그룹_시작일시가_종료일시보다_늦은_경우_수정_시_예외가_발생한다() {
		// given
		LocalDateTime invalidBeginDateTime = now.plusHours(3);
		LocalDateTime endDateTime = now.plusHours(2);

		// when & then
		assertThatThrownBy(() -> teamChallengeGroup.update(
			"그룹명",
			GroupAddress.of("서울시 서초구"),
			"설명",
			"https://chat.com",
			invalidBeginDateTime,
			endDateTime,
			10
		)).isInstanceOf(BusinessException.class);
	}

	@Test
	void 최대_참가자_수가_0_이하인_경우_수정_시_예외가_발생한다() {
		// given
		Integer invalidMaxParticipants = 0;

		// when & then
		assertThatThrownBy(() -> teamChallengeGroup.update(
			"그룹명",
			GroupAddress.of("서울시 서초구"),
			"설명",
			"https://chat.com",
			now.plusHours(1),
			now.plusHours(2),
			invalidMaxParticipants
		))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
	}

	@Test
	void 현재_참가자_수보다_작은_최대_참가자_수로_수정_시_예외가_발생한다() {
		// given
		// 현재 참가자 3명 추가
		teamChallengeGroup.addParticipant();
		teamChallengeGroup.addParticipant();
		teamChallengeGroup.addParticipant();

		Integer invalidMaxParticipants = 2; // 현재 참가자 3명보다 작음

		// when & then
		assertThatThrownBy(() -> teamChallengeGroup.update(
			"그룹명",
			GroupAddress.of("서울시 서초구"),
			"설명",
			"https://chat.com",
			now.plusHours(1),
			now.plusHours(2),
			invalidMaxParticipants
		))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage",
				ChallengeExceptionMessage.MAX_PARTICIPANTS_LESS_THAN_CURRENT);
	}

	@Test
	void 그룹_주소가_null인_경우_수정_시_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> teamChallengeGroup.update(
			"그룹명",
			null,
			"설명",
			"https://chat.com",
			now.plusHours(1),
			now.plusHours(2),
			10
		)).isInstanceOf(BusinessException.class);
	}
}
