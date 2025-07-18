package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;

class TeamChallengeGroupParticipationTest {

	private TeamChallengeGroupParticipation groupParticipation;
	private TeamChallengeParticipation participation;
	private TeamChallengeGroup teamChallengeGroup;
	private TeamChallenge teamChallenge;
	private Member member;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("google 123456789", "테스트유저", "test@example.com");

		// 테스트용 TeamChallenge 생성
		teamChallenge = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now),
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명"
		);

		// 테스트용 TeamChallengeGroup 생성
		teamChallengeGroup = TeamChallengeGroup.create(
			"테스트 그룹",
			now.minusHours(2),
			now.plusDays(6),
			10,
			GroupAddress.of("서울시 강남구"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			teamChallenge
		);

		// 테스트용 TeamChallengeParticipation 생성
		participation = TeamChallengeParticipation.create(
			teamChallenge,
			member,
			now.minusHours(1)
		);

		// 테스트용 TeamChallengeGroupParticipation 생성
		groupParticipation = TeamChallengeGroupParticipation.create(
			participation,
			teamChallengeGroup,
			GroupRoleType.MEMBER
		);
	}

	@Test
	void create_메서드로_그룹_참여를_생성할_수_있다() {
		// given
		TeamChallengeParticipation newParticipation = TeamChallengeParticipation.create(
			teamChallenge,
			member,
			now.minusMinutes(30)
		);

		// when
		TeamChallengeGroupParticipation newGroupParticipation = TeamChallengeGroupParticipation.create(
			newParticipation,
			teamChallengeGroup,
			GroupRoleType.LEADER
		);

		// then
		assertThat(newGroupParticipation.getTeamChallengeParticipation()).isEqualTo(newParticipation);
		assertThat(newGroupParticipation.getTeamChallengeGroup()).isEqualTo(teamChallengeGroup);
		assertThat(newGroupParticipation.getGroupRoleType()).isEqualTo(GroupRoleType.LEADER);
	}

	@Test
	void 참여_정보가_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeGroupParticipation.create(
			null,
			teamChallengeGroup,
			GroupRoleType.MEMBER
		))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 그룹이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeGroupParticipation.create(
			participation,
			null,
			GroupRoleType.MEMBER
		))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 역할이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeGroupParticipation.create(
			participation,
			teamChallengeGroup,
			null
		))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 역할을_변경할_수_있다() {
		// given
		GroupRoleType newRole = GroupRoleType.LEADER;

		// when
		groupParticipation.changeRole(newRole);

		// then
		assertThat(groupParticipation.getGroupRoleType()).isEqualTo(newRole);
	}

	@Test
	void 새로운_역할이_null이면_역할_변경_시_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> groupParticipation.changeRole(null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 그룹에서_탈퇴할_수_있다() {
		// given
		int initialParticipants = teamChallengeGroup.getCurrentParticipants();

		// when
		groupParticipation.leave();

		// then
		assertThat(teamChallengeGroup.getCurrentParticipants()).isEqualTo(initialParticipants - 1);
	}

	@Test
	void 리더가_다른_멤버가_있을_때_탈퇴하면_예외가_발생한다() {
		// given
		groupParticipation.changeRole(GroupRoleType.LEADER);
		// 다른 멤버 추가를 위해 참여자 수 증가 시뮬레이션
		teamChallengeGroup.addParticipant();

		// when & then
		assertThatThrownBy(() -> groupParticipation.leave())
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 리더인지_확인할_수_있다() {
		// given
		groupParticipation.changeRole(GroupRoleType.LEADER);

		// when & then
		assertThat(groupParticipation.isLeader()).isTrue();
	}

	@Test
	void 멤버인지_확인할_수_있다() {
		// given
		groupParticipation.changeRole(GroupRoleType.MEMBER);

		// when & then
		assertThat(groupParticipation.isLeader()).isFalse();
	}
}
