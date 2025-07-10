package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;

/**
 * TeamChallengeParticipation 테스트
 */
class TeamChallengeParticipationTest {

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
			GroupStatus.PROCEEDING,
			now.minusHours(2),
			now.plusDays(6),
			10,
			"서울시 강남구",
			"테스트 그룹 설명",
			"https://openchat.example.com"
		);

		// 팀 챌린지에 그룹 추가
		teamChallenge.addChallengeGroup(teamChallengeGroup);

		// 테스트용 TeamChallengeParticipation 생성 (멤버로 참여)
		participation = TeamChallengeParticipation.createMember(
			teamChallengeGroup,
			member,
			now.minusHours(1)
		);
	}

	@Test
	void createLeader_메서드로_팀_리더_참여를_생성할_수_있다() {
		// given
		Member leader = Member.create("google 111111111", "팀리더", "leader@example.com");
		LocalDateTime participatedAt = now.minusMinutes(30);

		// when
		TeamChallengeParticipation leaderParticipation = TeamChallengeParticipation.createLeader(
			teamChallengeGroup,
			leader,
			participatedAt
		);

		// then
		assertThat(leaderParticipation.getTeamChallengeGroup()).isEqualTo(teamChallengeGroup);
		assertThat(leaderParticipation.getMember()).isEqualTo(leader);
		assertThat(leaderParticipation.getGroupRoleType()).isEqualTo(GroupRoleType.LEADER);
		assertThat(leaderParticipation.getParticipatedAt()).isEqualTo(participatedAt);
		assertThat(leaderParticipation.isLeader()).isTrue();
	}

	@Test
	void createMember_메서드로_팀원_참여를_생성할_수_있다() {
		// given
		Member teamMember = Member.create("google 222222222", "팀원", "member@example.com");
		LocalDateTime participatedAt = now.minusMinutes(15);

		// when
		TeamChallengeParticipation memberParticipation = TeamChallengeParticipation.createMember(
			teamChallengeGroup,
			teamMember,
			participatedAt
		);

		// then
		assertThat(memberParticipation.getTeamChallengeGroup()).isEqualTo(teamChallengeGroup);
		assertThat(memberParticipation.getMember()).isEqualTo(teamMember);
		assertThat(memberParticipation.getGroupRoleType()).isEqualTo(GroupRoleType.MEMBER);
		assertThat(memberParticipation.getParticipatedAt()).isEqualTo(participatedAt);
		assertThat(memberParticipation.isLeader()).isFalse();
	}

	@Test
	void 팀_챌린지_그룹이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeParticipation.createMember(
			null,
			member,
			now
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> TeamChallengeParticipation.createLeader(
			null,
			member,
			now
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 회원이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeParticipation.createMember(
			teamChallengeGroup,
			null,
			now
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> TeamChallengeParticipation.createLeader(
			teamChallengeGroup,
			null,
			now
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 참여_시각이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeParticipation.createMember(
			teamChallengeGroup,
			member,
			null
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> TeamChallengeParticipation.createLeader(
			teamChallengeGroup,
			member,
			null
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 리더는_isLeader_메서드에서_true를_반환한다() {
		// given
		Member leader = Member.create("google 333333333", "팀리더", "leader@example.com");

		TeamChallengeParticipation leaderParticipation = TeamChallengeParticipation.createLeader(
			teamChallengeGroup,
			leader,
			now.minusMinutes(30)
		);

		// when & then
		assertThat(leaderParticipation.isLeader()).isTrue();
		assertThat(leaderParticipation.getGroupRoleType()).isEqualTo(GroupRoleType.LEADER);
	}
}
